package com.aura.spark.mllib;

import com.aura.dao.JavaDBDao;
import com.aura.db.DBHelper;
import com.aura.model.Log;
import com.aura.spark.core.LogAnalysis;
import com.aura.util.FileUtil;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.StructType;
import scala.collection.mutable.ArrayBuffer;

import java.sql.Connection;
import java.sql.SQLException;

public class ChannelPredict extends LogAnalysis {

    private String modelPath;
    private int numFeatures;

    protected StructType schema = new StructType()
            .add("channel_id", "int", false)
            .add("uuid", "string", false)
            .add("ip", "string", false)
            .add("ts", "long", false);

    public static int predict(NaiveBayesModel model, HashingTF tf, String text) {
        ArrayBuffer<String> keywords = FileUtil.getTrainingString(text);
        Vector features = tf.transform(keywords);
        return (int) model.predict(features);
    }

    public void setModelParams(String modelPath, int numFeatures) {
        this.modelPath = modelPath;
        this.numFeatures = numFeatures;
    }

    @Override
    protected void process(JavaRDD<Log> logs) {
        // Load model
        Broadcast<NaiveBayesModel> model = jsc.broadcast(NaiveBayesModel.load(jsc.sc(), modelPath));
        Broadcast<HashingTF> tf = jsc.broadcast(new HashingTF(numFeatures));
        // Predict
        JavaRDD<Row> rows = logs.map(log -> {
            int genderId = predict(model.value(), tf.value(), log.getClearTitle());
            return RowFactory.create(genderId, log.getUuid(), log.getIp(), log.getTs());
        });
        Dataset<Row> df = spark.createDataFrame(rows, schema);
        df.createOrReplaceTempView("logs");
        Dataset<Row> counts = spark.sql("SELECT channel_id, toDayStr(ts) AS day, COUNT(1) AS pv, COUNT(DISTINCT uuid) AS uv, COUNT(DISTINCT ip) AS ip FROM logs GROUP BY channel_id, toDayStr(ts)");
        counts.foreachPartition(records -> {
            Connection conn = DBHelper.getConnection();
            records.forEachRemaining(row -> {
                try {
                    JavaDBDao.saveChannelCount(conn, row.getInt(0), row.getString(1), row.getLong(2), row.getLong(3), row.getLong(4));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            conn.close();
        });
    }

    /**
     * $SPARK_HOME/bin/spark-submit \
     *  --master yarn-cluster \
     *  --class com.aura.spark.mllib.GenderPredict \
     *  $jar_file $model_path $log_dir
     * @param args
     */
    public static void main(String[] args) {
        ChannelPredict channel = new ChannelPredict();
        channel.setModelParams(args[0], 10000);
        channel.runAnalysis(args[1]);
    }
}
