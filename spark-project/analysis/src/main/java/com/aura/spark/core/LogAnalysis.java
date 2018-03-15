package com.aura.spark.core;

import com.alibaba.fastjson.JSON;
import com.aura.model.Log;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.text.SimpleDateFormat;
import java.util.Date;

abstract public class LogAnalysis {

    final protected JavaSparkContext jsc;
    final protected SparkSession spark;

    public LogAnalysis() {
        SparkConf conf = new SparkConf();
        conf.setIfMissing("spark.app.name", getClass().getSimpleName());
        spark = SparkSession.builder().config(conf).getOrCreate();
        jsc = new JavaSparkContext(spark.sparkContext());
    }

    final public void runAnalysis(String path) {
        spark.udf().register("toDayStr", new ToDayFunction(), DataTypes.StringType);
        JavaRDD<Log> logs = parseLogFile(path);
        JavaRDD<Log> validLogs = filterValidLogs(logs);

        process(validLogs);

        spark.close();
    }

    protected abstract void process(JavaRDD<Log> logs);

    private JavaRDD<Log> parseLogFile(String path) {
        JavaRDD<String> lines = jsc.textFile(path);
        JavaRDD<Log> logs = lines.map(line -> JSON.parseObject(line, Log.class));
        return logs.filter(l -> l != null && l.isLegal());
    }

    private JavaRDD<Log> filterValidLogs(JavaRDD<Log> logs) {
        return logs.filter(log -> log.getPageType() != '1' && log.getClearTitle() != null && log.getClearTitle().length() > 5);
    }

    protected StructType schema = new StructType()
            .add("content_id", "long", false)
            .add("uuid", "string", false)
            .add("url", "string", true)
            .add("title", "string", true)
            .add("ip", "string", true)
            .add("country", "string", true)
            .add("area", "string", true)
            .add("ts", "long", true);

    protected Dataset<Row> toDataFrame(JavaRDD<Log> logs) {
        JavaRDD<Row> rowsRDD = logs.map(log -> RowFactory.create(
                log.getContentId(),
                log.getUuid(),
                log.getUrl(),
                log.getClearTitle(),
                log.getIp(),
                log.getCountry(),
                log.getArea(),
                log.getTs())
        );
        return spark.createDataFrame(rowsRDD, schema);
    }
}

class ToDayFunction implements UDF1<Long, String> {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static String toDay(long seconds) {
        return format.format(new Date(seconds * 1000));
    }

    @Override
    public String call(Long s) throws Exception {
        return toDay(s);
    }
}