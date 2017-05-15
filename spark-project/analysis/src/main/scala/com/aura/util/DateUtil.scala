package com.aura.util

import java.util.Calendar
import java.text.SimpleDateFormat

object DateUtil {
  
  def main(args: Array[String]): Unit = {
    println(getSecond())
  }

	/**
		* 得到当前的日期
		* @return
		*/
	def getDay(): String = {
		val calendar:Calendar = Calendar.getInstance()
		val sdf:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
		sdf.format(calendar.getTime())
	}

	/**
		* 得到当前的秒
		* @return
		*/
	def getSecond(): Int = {
	  val calendar:Calendar = Calendar.getInstance()
    val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
    val minute: Int = calendar.get(Calendar.MINUTE)
    val secondOfHour: Int = calendar.get(Calendar.SECOND)
    val second: Int = hour * 3600 + minute * 60 + secondOfHour
    second
	}
}