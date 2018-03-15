package com.aura.spark.core;

import com.aura.dao.JavaDBDao;
import com.aura.db.DBHelper;
import com.aura.model.Log;
import static com.aura.spark.core.ToDayFunction.toDay;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import scala.Tuple2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JavaFlowAnalysis extends LogAnalysis {

    @Override
    protected void process(JavaRDD<Log> logs) {
        logs.cache();
        Dataset<Row> logDF = toDataFrame(logs);
        logDF.createOrReplaceTempView("logs");
        // There is a bug when using udf in group by clause in Spark 2.1.0, fixed in 2.1.1 and 2.2.0
        // See detail https://issues.apache.org/jira/browse/SPARK-9435
        Dataset<Row> counts = spark.sql("SELECT toDayStr(ts) AS day, COUNT(1) AS pv, COUNT(DISTINCT uuid) AS uv, COUNT(DISTINCT ip) AS ip FROM logs GROUP BY toDayStr(ts)");

        counts.foreachPartition(rows -> {
            Connection conn = DBHelper.getConnection();
            rows.forEachRemaining(row -> {
                try {
                    JavaDBDao.saveDimensionCount(conn, 0, row.getString(0), row.getLong(1), row.getLong(2), row.getLong(3));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            conn.close();
        });

        JavaPairRDD<String, Long> totalTimes = sumTotalTime(logs);

        totalTimes.foreachPartition(times -> {
            Connection conn = DBHelper.getConnection();
            times.forEachRemaining(t -> {
                try {
                    JavaDBDao.updateDimensionTime(conn, 0, t._1, t._2);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            conn.close();
        });
    }

    private JavaPairRDD<String, Long> sumTotalTime(JavaRDD<Log> logs) {
        JavaPairRDD<String, Long> userTimes = logs
                .mapToPair(log -> new Tuple2<String, Long>(toDay(log.getTs())+":"+log.getUuid(), log.getTs()));
        JavaPairRDD<String, Long> userTotalTimes =
                userTimes.groupByKey().mapValues(new TimeIntervalAggFunction());
        return userTotalTimes.mapToPair(t -> {
            String day = t._1.split(":")[0];
            return new Tuple2<String, Long>(day, t._2);
        }).reduceByKey((v1, v2) -> v1 + v2);
    }

    /**
     * $SPARK_HOME/bin/spark-submit \
     *  --master yarn-cluster \
     *  --class com.aura.spark.core.JavaFlowAnalysis \
     *  $jar_file $log_dir
     * @param args
     */
    public static void main(String[] args) {
        JavaFlowAnalysis flow = new JavaFlowAnalysis();
        flow.runAnalysis(args[0]);
    }
}

class TimeIntervalAggFunction implements Function<Iterable<Long>, Long> {
    @Override
    public Long call(Iterable<Long> itr) throws Exception {
        List<Long> times = new ArrayList<Long>();
        itr.forEach(times::add);
        long sum = 10;
        if (times.size() > 1) {
            Collections.sort(times);
            for (int i = 1; i < times.size(); i++) {
                long interval = times.get(i) - times.get(i - 1);
                if (interval > 1800) {
                    interval = 10;
                }
                sum += interval;
            }
        }
        return sum;
    }
}