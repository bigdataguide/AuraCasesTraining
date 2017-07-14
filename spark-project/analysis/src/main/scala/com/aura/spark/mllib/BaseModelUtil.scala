package com.aura.spark.mllib

/**
  * Created by zheng.tan on 17/02/2017.
  */
object BaseModelUtil {

  private val DEFAULT_MODEL_PATH = "spark-project/analysis/data/model"

  def modelPath(typ: String, path: String = DEFAULT_MODEL_PATH) = {
    path + "/" + typ
  }

  def main(args: Array[String]): Unit = {
    ChannelModel.main(args)
    GenderModel.main(args)
  }

}
