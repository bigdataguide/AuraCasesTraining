package com.aura.spark.mllib;

import com.aura.util.JavaStringUtil;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.Saveable;

public abstract class LogTraining {

    protected JavaSparkContext jsc;

    public LogTraining() {
        SparkConf conf = new SparkConf();
        conf.setAppName(getClass().getSimpleName());
        jsc = new JavaSparkContext(conf);
    }

    public void runTraining(String inputDataPath, String outputModelPath) {
        JavaRDD<String> lines = jsc.textFile(inputDataPath);
        Broadcast<HashingTF> broadcastTF = jsc.broadcast(new HashingTF(10000));
        JavaRDD<LabeledPoint> data = lines.map(line -> {
            String[] arr = line.split(",");
            Double label = Double.parseDouble(arr[0]);
            Vector features = broadcastTF.value().transform(JavaStringUtil.extractKeywords(arr[1]));
            return new LabeledPoint(label, features);
        });
        Saveable model = trainModel(data);
        model.save(jsc.sc(), outputModelPath);
        jsc.stop();
    }

    protected abstract Saveable trainModel(JavaRDD<LabeledPoint> data);
}
