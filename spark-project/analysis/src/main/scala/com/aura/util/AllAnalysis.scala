package com.aura.util

import com.aura.config.Config
import com.aura.spark.core._
import com.aura.spark.mllib.{ChannelAnalysis, GenderAnalysis}

object AllAnalysis {

  def main(args: Array[String]): Unit = {

    val days = Array("2016-12-01", "2016-12-02", "2016-12-03", "2016-12-04", "2016-12-05", "2016-12-06", "2016-12-07")

    days.foreach { day =>

      Config.setDay(day)
      println(s"Run All Analysis for $day")

      FlowAnalysis.runAnalysis()
      SearchAnalysis.runAnalysis()
      ProvinceAnalysis.runAnalysis()
      CountryAnalysis.runAnalysis()
      ContentAnalysis.runAnalysis()

      // need to train model before run the following analysis
      GenderAnalysis.runAnalysis()
      ChannelAnalysis.runAnalysis()
    }

  }

}