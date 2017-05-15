package com.aura.config

import org.apache.commons.configuration.{ConfigurationException, PropertiesConfiguration}

object Config {
  /**
    * mysql数据库配置
    */
  var driver_class: String = null
  var db_url: String = null
  var username: String = null
  var password: String = null
  var checkout_timeout: Int = 0
  /**
    * 静态参数配置
    */
  var day: String = null
  var output_path: String = "output/time"
  var newline: String = "\n"
  var backslash: String = "/"
  var default_ses_time: Long = 1800
  var reduce_result_filename: String = "part-r-00000"
  /**
    * Kafka参数
    */
  var topic: String = null
  var zkHosts: String = null
  var brokerList: String = null

  def setDay(dayStr: String): Unit = {
    this.day = dayStr
  }

  def input_path: String = {
    assert(day != null, "should set day before run")
    "data/logs/aura" + day.replace("-", "") + ".log"
  }

  private def loadConfig(config: PropertiesConfiguration) {
    if (config.containsKey("driver_class")) {
      driver_class = config.getString("driver_class")
    }
    if (config.containsKey("db_url")) {
      db_url = config.getString("db_url")
    }
    if (config.containsKey("username")) {
      username = config.getString("username")
    }
    if (config.containsKey("password")) {
      password = config.getString("password")
    }
    if (config.containsKey("checkout_timeout")) {
      checkout_timeout = config.getInt("checkout_timeout")
    }

    if (config.containsKey("topic")) {
      topic = config.getString("topic")
    }
    if (config.containsKey("zkHosts")) {
      zkHosts = config.getString("zkHosts")
    }
    if (config.containsKey("brokerList")) {
      brokerList = config.getString("brokerList")
    }
  }

  def main(args: Array[String]) {
    System.out.println(Config.day)
    System.out.println(Config.input_path)
    System.out.println(Config.driver_class)
    System.out.println(Config.db_url)
    System.out.println(Config.username)
    System.out.println(Config.password)
    System.out.println(Config.checkout_timeout)
    System.out.println(Config.topic)
    System.out.println(Config.zkHosts)
    System.out.println(Config.brokerList)
  }

  try {
    var config: PropertiesConfiguration = null
    try {
      config = new PropertiesConfiguration("aura.properties")
    }
    catch {
      case e: ConfigurationException => {
        e.printStackTrace()
      }
    }
    loadConfig(config)
  }
}