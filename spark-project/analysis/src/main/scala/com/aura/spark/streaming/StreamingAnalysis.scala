package com.aura.spark.streaming

import kafka.serializer.StringDecoder

import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream._
import org.apache.spark.streaming.kafka._
import org.apache.spark.broadcast.Broadcast
import com.alibaba.fastjson.JSON
import java.sql.Connection
import java.util.Calendar

import scala.util.control.NonFatal

import com.aura.config.Config
import com.aura.dao.{ContentDao, DimensionDao}
import com.aura.db.DBHelper
import com.aura.entity.{Content, Dimension, Log}
import com.aura.util.{SparkUtil, StringUtil}

object StreamingAnalysis {

  /**
    * 省份维度计算
    * @param filter
    * @param bc
    * @return
    */
  def dimensionDStream(filter: DStream[Log], bc: Broadcast[Map[String, Dimension]]): DStream[(String, Dimension)] = {
    val map = filter.map(
        log => {
          val dimension: Dimension = new Dimension(pv = 1,uv = 1,ip = 1)
          val second = getSecond(log)
          dimension.second = second
          val area = log.Area
          dimension.uvs += log.Uuid
          if(area != null) {
            if(bc.value.contains(area)) {
              dimension.dimeId = bc.value.get(area).get.dimeId
            }
          }
          (dimension.dimeId + " - " + dimension.second, dimension)
        }
    ).filter(_._1 != 0).cache()
    
    map.reduceByKey((m, n) => {
      m.pv += n.pv
      m.uvs ++= n.uvs
      m.uv = m.uvs.size
      (m)
    })
  }

  /**
    * 省份维度写库
    * @param result
    */
  def dimensionSaveDB(result: DStream[(String, Dimension)]) {
    result.foreachRDD(
      rdd => {
        val list: List[Dimension] = rdd.values.collect().toList
        if(list != null && list.size > 0) {
          DimensionDao.saveStreamingDimensionList(list)
        }
      }
    )
  }

  /**
    * 稿件热度计算
    * @param filter
    * @return
    */
  def contentDStream(filter: DStream[Log]): DStream[(String, Content)] = {
    val map = filter.filter{log => {log.getPagetype() == '1' && log.getClearTitle() != null && log.getClearTitle().length() > 5}}.map(
        log => {
          val content: Content = new Content(pv = 1, uv = 1)
          val second = getSecond(log)
          content.second = second
          content.contentId = log.ContentId
          content.uvs += log.Uuid
          content.url = log.Url
          content.title = log.getClearTitle()
          (content.contentId + " - " + content.second, content)
        }
      ).cache()
    
    map.reduceByKey((m, n) => {
      m.pv += n.pv
      m.uvs ++= n.uvs
      m.uv = m.uvs.size
      (m)
    })
  }

  /**
    * 稿件热度写库
    * @param result
    */
  def contentSaveDB(result: DStream[(String, Content)]) {
     result.foreachRDD(
      rdd => {
        val list: List[Content] = rdd.values.collect().toList
        if(list != null && list.size > 0) {
          list.foreach(item => {
            item.url = StringUtil.limitString(item.url, 500, "utf8")
            item.title = StringUtil.limitString(item.title, 500, "utf8")
          })
          val conn: Connection = DBHelper.getConnection()
          try {
            ContentDao.saveStreamingContentData(list, conn)
            ContentDao.saveStreamingContentDetail(list, conn)
          } finally {
            DBHelper.close(conn)
          }
        }
      }
    )
  }

  /**
    * 解析日志并过滤其中的错误内容
    */
  def getFilterLog(stream: InputDStream[(String, String)]): DStream[Log] = {
    stream.flatMap(line => parseLog(line._2))
  }

  def parseLog(line: String): Option[Log] = {
    try {
      val log = JSON.parseObject(line, classOf[Log])
      if (log.isLegal()) Some(log) else None
    } catch {
      case NonFatal(e) => None
    }
  }
  
  def main(args: Array[String]): Unit = {
    val second = 1
    val brokerList: String = Config.brokerList
    val topic: String = Config.topic
    val conf = SparkUtil.getSparkConf(this.getClass)

    val ssc = new StreamingContext(conf, Seconds(second))
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokerList, "auto.offset.reset" -> "smallest", "group.id" -> "tvStreaming")
    val stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, Set(topic))

    /**
      * 省份映射
      */
    val provinceMap: Map[String, Dimension] = DimensionDao.getProvinceMap()
    /**
     * 广播变量
     */
    val provinceBc: Broadcast[Map[String, Dimension]] = stream.context.sparkContext.broadcast(provinceMap)
    
    /**
     * 解析日志并过滤其中的错误内容
     */
    val filter = getFilterLog(stream)
    /**
      * 省份维度计算
      * 稿件热度计算
      */
    val result: DStream[(String, Dimension)] = dimensionDStream(filter, provinceBc)
    val result2: DStream[(String, Content)] = contentDStream(filter)
    /**
      * 省份维度写库
      * 稿件热度写库
      */
    dimensionSaveDB(result)
    contentSaveDB(result2)
    
    ssc.start()
    ssc.awaitTermination()
  }

  /**
    * 得到当前的秒
    * @return second
    */
  private def getSecond(log: Log): Int = {
    val ts: Long = log.getTs
    val millisecond: String = ts + "000"
    val calendar: Calendar = Calendar.getInstance
    calendar.setTimeInMillis(millisecond.toLong)
    val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
    val minute: Int = calendar.get(Calendar.MINUTE)
    val secondOfHour: Int = calendar.get(Calendar.SECOND)
    val second: Int = hour * 3600 + minute * 60 + secondOfHour
    second
  }
}