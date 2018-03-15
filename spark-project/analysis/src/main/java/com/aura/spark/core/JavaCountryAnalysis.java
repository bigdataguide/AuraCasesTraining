package com.aura.spark.core;

import com.aura.dao.JavaDBDao;
import com.aura.db.DBHelper;
import com.aura.model.Log;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.StructType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class JavaCountryAnalysis extends LogAnalysis {

    protected StructType schema = new StructType()
            .add("dim_id", "int", false)
            .add("uuid", "string", false)
            .add("ip", "string", false)
            .add("ts", "long", false);

    @Override
    protected void process(JavaRDD<Log> logs) {
        Map<String, Integer> countryMap = JavaDBDao.getCountryMap();
        Broadcast<Map<String, Integer>> broadcastCountryMap = jsc.broadcast(countryMap);
        JavaRDD<Row> rowRDD = logs.map(log -> {
            int dimId = broadcastCountryMap.value().getOrDefault(log.getCountry(), -1);
            return RowFactory.create(dimId, log.getUuid(), log.getIp(), log.getTs());
        });
        Dataset<Row> logDF = spark.createDataFrame(rowRDD, schema);
        logDF.createOrReplaceTempView("logs");
        Dataset<Row> counts = spark.sql("SELECT dim_id, toDayStr(ts) AS day, COUNT(1) AS pv, COUNT(DISTINCT uuid) AS uv, COUNT(DISTINCT ip) AS ip FROM logs GROUP BY dim_id, toDayStr(ts)");
        counts.foreachPartition(rows -> {
            Connection conn = DBHelper.getConnection();
            rows.forEachRemaining(row -> {
                try {
                    JavaDBDao.saveDimensionCount(conn, row.getInt(0), row.getString(1), row.getLong(2), row.getLong(3), row.getLong(4));
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
     *  --class com.aura.spark.core.JavaCountryAnalysis \
     *  $jar_file $log_dir
     * @param args
     */
    public static void main(String[] args) {
        JavaCountryAnalysis country = new JavaCountryAnalysis();
        country.runAnalysis(args[0]);
    }
}
