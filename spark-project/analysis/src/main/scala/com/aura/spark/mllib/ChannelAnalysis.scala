package com.aura.spark.mllib

import com.aura.config.Config
import com.aura.dao.TrainingDao
import com.aura.entity.Training
import com.aura.util.{FileUtil, SparkUtil}

import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.feature.HashingTF

/**
  * 新闻频道分类计算
  */
object ChannelAnalysis {

  def runAnalysis(): Unit = {
    /**
      * 获得SparkContext
      */
    val sc = SparkUtil.getSparkContext(this.getClass)
    /**
      * 读取日志
      */
    val lines = sc.textFile(Config.input_path)
    /**
      * 解析日志并过滤其中的错误内容
      */
    val filter = SparkUtil.getFilterLog(lines).cache()

    val model = NaiveBayesModel.load(sc, BaseModelUtil.modelPath("bayes"))
    val tf = new HashingTF(numFeatures = 10000)

    /**
      * 计算频道分类信息(map,reduce)
      */
    val map = filter.map(
      log => {
        val training: Training = new Training(pv = 1,uv = 1,ip = 1)
        training.channelId = model.predict(tf.transform(FileUtil.getTrainingString(log.getClearTitle()))).toInt

        training.uvs += log.Uuid
        training.ips += log.Ip
        (training.channelId, training)
      }
    ).cache()
    /**
      * 计算维度pv,uv,ip的通用ReduceByKey
      */
    val reduce = map.reduceByKey((m, n) => {
      m.pv += n.pv
      m.uvs ++= n.uvs
      m.ips ++= n.ips
      m.uv = m.uvs.size
      m.ip = m.ips.size
      (m)
    })

    val list: List[Training] = reduce.values.collect().toList
    list.foreach(item => {
      item.day = Config.day
    })
    sc.stop()
    /**
      * 写入数据库
      */
    TrainingDao.saveChannelList(list)
  }

  def main(args: Array[String]): Unit = {
    val dayStr = if (args.length > 0) args(0) else "2016-12-01"
    Config.setDay(dayStr)
    runAnalysis()
  }
}
