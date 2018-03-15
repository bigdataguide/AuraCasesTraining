package com.aura.spark.mllib;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.Saveable;

public class GenderTraining extends LogTraining {

    @Override
    protected Saveable trainModel(JavaRDD<LabeledPoint> data) {
        return SVMWithSGD.train(data.rdd(), 100);
    }

    /**
     * $SPARK_HOME/bin/spark-submit \
     *  --master yarn-cluster \
     *  --class com.aura.spark.mllib.GenderTraining \
     *  $jar_file $input_data_path $output_model_path
     * @param args
     */
    public static void main(String[] args) {
        GenderTraining gender = new GenderTraining();
        gender.runTraining(args[0], args[1]);
    }
}
