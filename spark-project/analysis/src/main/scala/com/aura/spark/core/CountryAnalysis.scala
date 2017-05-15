package com.aura.spark.core

import com.aura.config.Config
import com.aura.dao.DimensionDao
import com.aura.entity.Dimension
import com.aura.util.SparkUtil

import org.apache.spark.broadcast.Broadcast

/**
  * Created by An on 2016/11/25.
  * 国家分布计算
  */
object CountryAnalysis {

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
    /**
      * 国家映射
      */
    val countryMap: Map[String, Dimension] = DimensionDao.getCountryMap()
    /**
      * 广播变量
      */
    val countryBc: Broadcast[Map[String, Dimension]] = sc.broadcast(countryMap)

    /**
      * 计算国家分布(map,reduce)
      */
    val map = filter.map(
      log => {
        val dimension: Dimension = new Dimension(pv = 1,uv = 1,ip = 1)
        val country = log.Country
        dimension.uvs += log.Uuid
        dimension.ips += log.Ip
        if(country != null) {
          if(countryBc.value.contains(country)) {
            dimension.dimeId = countryBc.value.get(country).get.dimeId
          }
        }
        (dimension.dimeId, dimension)
      }
    ).filter(_._1 != 0).cache()
    /**
      * 计算维度pv,uv,ip的通用ReduceByKey
      */
    val reduce = SparkUtil.getReduceByKey(map)

    val list: List[Dimension] = reduce.values.collect().toList
    list.foreach(item => {
      item.day = Config.day
    })
    sc.stop()
    /**
      * 写入数据库
      */
    DimensionDao.saveDimensionList(list)
  }

  def main(args: Array[String]): Unit = {
    val dayStr = if (args.length > 0) args(0) else "2016-12-01"
    Config.setDay(dayStr)
    runAnalysis()
  }
}
