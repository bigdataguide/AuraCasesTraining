package com.aura.db

import java.sql.{Connection, DriverManager}
import com.aura.config.Config

object JDBCHelper {
  
  def getConnection(): Connection = {
    Class.forName(Config.driver_class)
    DriverManager.getConnection(Config.db_url, Config.username, Config.password)
	}
  
  def main(args: Array[String]): Unit = {
    JDBCHelper.getConnection()
  }
}