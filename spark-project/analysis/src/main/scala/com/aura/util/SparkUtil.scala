package com.aura.util

import com.alibaba.fastjson.JSON
import com.aura.config.Config
import com.aura.entity.{Dimension, Log}

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object SparkUtil {
  /**
    * 获得SparkConf
    * @param cls
    * @return
    */
  def getSparkConf(cls: Class[_]): SparkConf = {
    new SparkConf()
      .setAppName(cls.getSimpleName())
      .setIfMissing("spark.master", "local[*]")
  }

  /**
    * 获得SparkContext
    * @param cls
    * @return
    */
  def getSparkContext(cls: Class[_]): SparkContext = {
    new SparkContext(getSparkConf(cls))
  }

  /**
    * 解析日志并过滤其中的错误内容
    * @param lines
    * @return
    */
  def getFilterLog(lines: RDD[String]): RDD[Log] = {
    lines.map(
      line => {
        val log: Log = JSON.parseObject(line, classOf[Log])
        if(log.isLegal()) {
          (log)
        } else {
          (null)
        }
      }
    ).filter(_ != null)
  }

  /**
    * 计算维度pv,uv,ip的通用ReduceByKey
    * 页面浏览量直接累加
    * 访问者和访问者IP数需要归并
    * @param map
    * @return
    */
  def getReduceByKey(map: RDD[(Int,Dimension)]): RDD[(Int,Dimension)] = {
    map.reduceByKey((m, n) => {
      m.pv += n.pv
      m.uvs ++= n.uvs
      m.ips ++= n.ips
      m.uv = m.uvs.size
      m.ip = m.ips.size
      (m)
    })
  }
}
