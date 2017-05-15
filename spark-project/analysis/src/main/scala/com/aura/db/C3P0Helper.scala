package com.aura.db

import java.sql.Connection
import com.aura.config.Config
import com.mchange.v2.c3p0.ComboPooledDataSource

object C3P0Helper {

  private var cpds: ComboPooledDataSource = _
  /**
    * 获得c3p0连接
    *
    * @return
    */
  def getConnection(): Connection = {
    if (cpds == null) {
      synchronized {
        if (cpds == null) {
          Class.forName(Config.driver_class)
          cpds = initCPDS(Config.db_url)
        }
      }
    }
    cpds.getConnection
  }

  /**
    * 初始化c3p0数据源
    *
    * @param url
    */
  private def initCPDS(url: String): ComboPooledDataSource = {
    // 批量提交
    var jdbcUrl = ""
    if (url.indexOf("?") < 0)
      jdbcUrl = url + "?rewriteBatchedStatements=true"
    else
      jdbcUrl = url + "&rewriteBatchedStatements=true"
    val thecpds: ComboPooledDataSource = new ComboPooledDataSource
    thecpds.setJdbcUrl(jdbcUrl)
    thecpds.setUser(Config.username)
    thecpds.setPassword(Config.password)
    thecpds.setCheckoutTimeout(Config.checkout_timeout)
    // the settings below are optional -- c3p0 can work with defaults
    thecpds.setMinPoolSize(1)
    thecpds.setInitialPoolSize(1)
    thecpds.setAcquireIncrement(1)
    thecpds.setMaxPoolSize(5)
    thecpds.setMaxIdleTime(1800)
    thecpds.setMaxStatements(0) // disable Statements cache to avoid deadlock
    thecpds.setPreferredTestQuery("select 1")
    thecpds.setTestConnectionOnCheckout(true)
    thecpds
  }

//  @throws[Exception]
//  def main(args: Array[String]) {
//    C3P0Helper.getConnection
//  }
}
