package com.aura.spark.core;

import com.aura.dao.JavaDBDao;
import com.aura.db.DBHelper;
import com.aura.model.Log;
import com.aura.util.StringUtil;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class JavaContentAnalysis extends LogAnalysis {

    private static final Logger LOG = LoggerFactory.getLogger(JavaContentAnalysis.class);

    @Override
    protected void process(JavaRDD<Log> logs) {
        Dataset<Row> logDF = toDataFrame(logs);
        logDF.cache();
        logDF.createOrReplaceTempView("logs");
        Dataset<Row> contentCounts = spark.sql("SELECT content_id, toDayStr(ts) AS day, COUNT(1) AS pv, COUNT(DISTINCT uuid) AS uv FROM logs GROUP BY content_id,toDayStr(ts)");
        contentCounts.foreachPartition(rows -> {
            Connection conn = DBHelper.getConnection();
            rows.forEachRemaining(row -> {
                try {
                    JavaDBDao.saveContentCount(conn, row.getLong(0), row.getString(1), row.getLong(2), row.getLong(3));
                } catch (SQLException e) {
                    LOG.error("save content count failed", e);
                }
            });
            conn.close();
        });

        Dataset<Row> contentDetails = spark.sql("SELECT DISTINCT content_id, url, title FROM logs");
        contentDetails.foreachPartition(rows -> {
            Connection conn = DBHelper.getConnection();
            rows.forEachRemaining(row -> {
                try {
                    String url = StringUtil.limitString(row.getString(1), 500, "utf8");
                    String title = StringUtil.limitString(row.getString(2), 500, "utf8");
                    JavaDBDao.saveContentDetail(conn, row.getLong(0), url, title);
                } catch (SQLException e) {
                    LOG.error("save content detail failed", e);
                }
            });
            conn.close();
        });
    }

    /**
     * $SPARK_HOME/bin/spark-submit \
     *  --master yarn-cluster \
     *  --class com.aura.spark.core.JavaContentAnalysis \
     *  $jar_file $log_dir
     *
     * @param args
     */
    public static void main(String[] args) {
        JavaContentAnalysis content = new JavaContentAnalysis();
        content.runAnalysis(args[0]);
    }
}
