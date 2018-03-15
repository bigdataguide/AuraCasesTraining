package com.aura.spark.mllib;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.Saveable;

public class ChannelTraining extends LogTraining {

    @Override
    protected Saveable trainModel(JavaRDD<LabeledPoint> data) {
        return NaiveBayes.train(data.rdd(), 1.0, "multinomial");
    }

    /**
     * $SPARK_HOME/bin/spark-submit \
     *  --master yarn-cluster \
     *  --class com.aura.spark.mllib.ChannelTraining \
     *  $jar_file $input_data_path $output_model_path
     * @param args
     */
    public static void main(String[] args) {
        ChannelTraining channel = new ChannelTraining();
        channel.runTraining(args[0], args[1]);
    }
}
