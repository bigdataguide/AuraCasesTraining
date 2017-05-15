package com.aura.spark.mllib

import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import scala.collection.mutable.ListBuffer

import com.aura.entity.Training
import com.aura.util.{FileUtil, SparkUtil}
import org.apache.hadoop.fs.{FileSystem, Path}

/**
  * 支持向量积算法
  * 生成模型
  */
object GenderModel {

  def main(args: Array[String]) {
    val sc = SparkUtil.getSparkContext(this.getClass)

    val modelPath = new Path(BaseModelUtil.modelPath("svm"))
    val fs: FileSystem = FileSystem.get(modelPath.toUri, sc.hadoopConfiguration)
    if (fs.exists(modelPath)) {
      fs.delete(modelPath, true)
    }

    val list: ListBuffer[Training] = FileUtil.getTrainingList("data/ml/gender.txt")
    val arr = FileUtil.getTrainingArrayBuffer(list)

    val data = sc.parallelize(arr)
    val tf = new HashingTF(numFeatures = 10000)
    val parsedData = data.map{ line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, tf.transform(parts(1).split(" ")))
    }.cache()

    val model = SVMWithSGD.train(parsedData, 100)

    // 保存模型
    model.save(sc, BaseModelUtil.modelPath("svm"))

    sc.stop
  }
}
