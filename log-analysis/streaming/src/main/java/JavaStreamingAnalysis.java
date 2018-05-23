import com.google.common.collect.Sets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import redis.clients.jedis.Jedis;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by qianxi.zhang on 5/23/18.
 */
public class JavaStreamingAnalysis {
  private Config config;
  private JavaStreamingContext ssc;

  private static final String PROVINCE_HASHKEY = "province";
  private static final String WEBSITE_HASHKEY = "website";

  public JavaStreamingAnalysis() {
    config = ConfigFactory.parseResources("streaming.conf");
  }

  private JavaStreamingContext createStreamingContext(Config config) {
    SparkConf conf = new SparkConf();
    conf.setAppName("Java Streaming Analysis");
    conf.set("spark.streaming.stopGracefullyOnShutdown", "true");
    conf.setMaster("local[4]");
    Duration batchInterval = Durations.seconds(config.getLong("streaming.interval"));
    JavaStreamingContext ssc = new JavaStreamingContext(conf, batchInterval);
    return ssc;
  }

  private Map<String, String> getKafkaParams() {
    Map<String, String> params = new HashMap<String, String>();
    Config kafkaConfig = config.getConfig("kafka");
    params.put("metadata.broker.list", kafkaConfig.getString("metadata.broker.list"));
    params.put("auto.offset.reset", kafkaConfig.getString("auto.offset.reset"));
    params.put("group.id", kafkaConfig.getString("group.id"));
    return params;
  }

  public void runAnalysis() throws InterruptedException {
    ssc = createStreamingContext(config);
    ssc.sparkContext().setLogLevel("WARN");
    String topic = config.getString("streaming.topic");
    JavaPairInputDStream<String, String> input = KafkaUtils.createDirectStream(
        ssc,
        String.class,
        String.class,
        StringDecoder.class,
        StringDecoder.class,
        getKafkaParams(),
        Sets.newHashSet(topic));

    processByProvince(input);
    processByWebsite(input);

    ssc.start();
    ssc.awaitTermination();
  }

  public static void processByProvince(JavaPairInputDStream<String, String> input) {
    JavaPairDStream<String, Long> provinceMap = input.mapToPair(x -> {
      String price = "0";
      String province = "other";

      String[] attributes_list = x._2.split(",");

      if (attributes_list.length == 12) {
        price = attributes_list[4];
        province = attributes_list[6];
      }
      return new Tuple2<>(province, Long.valueOf(price));
    }).reduceByKey((x, y) -> x + y);

    provinceMap.foreachRDD(new VoidFunction<JavaPairRDD<String, Long>>() {
      @Override
      public void call(JavaPairRDD<String, Long> rdd) throws Exception {
        rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Long>>>() {
          @Override
          public void call(Iterator<Tuple2<String, Long>> partitionOfRecords) throws Exception {
            Jedis jedis = JavaRedisClient.get().getResource();
            while (partitionOfRecords.hasNext()) {
              try {
                Tuple2<String, Long> pair = partitionOfRecords.next();
                String province = pair._1();
                long price = pair._2();
                jedis.hincrBy(PROVINCE_HASHKEY, province, price);
                System.out.println("Update province " + province + " to " + price);
              } catch (Exception e) {
                System.out.println("error:" + e);
              }
            }
            jedis.close();
          }
        });
      }
    });
  }


  public static void processByWebsite(JavaPairInputDStream<String, String> input) {
    JavaPairDStream<String, Long> provinceMap = input.mapToPair(x -> {
      String price = "0";
      String website = "other";

      String[] attributes_list = x._2.split(",");

      if (attributes_list.length == 12) {
        price = attributes_list[4];
        website = attributes_list[7];
      }
      return new Tuple2<>(website, Long.valueOf(price));
    }).reduceByKey((x, y) -> x + y);

    provinceMap.foreachRDD(new VoidFunction<JavaPairRDD<String, Long>>() {
      @Override
      public void call(JavaPairRDD<String, Long> rdd) throws Exception {
        rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Long>>>() {
          @Override
          public void call(Iterator<Tuple2<String, Long>> partitionOfRecords) throws Exception {
            Jedis jedis = JavaRedisClient.get().getResource();
            while (partitionOfRecords.hasNext()) {
              try {
                Tuple2<String, Long> pair = partitionOfRecords.next();
                String website = pair._1();
                long price = pair._2();
                jedis.hincrBy(WEBSITE_HASHKEY, website, price);
                System.out.println("Update province " + website + " to " + price);
              } catch (Exception e) {
                System.out.println("error:" + e);
              }
            }
            jedis.close();
          }
        });
      }
    });
  }

  /**
   * $SPARK_HOME/bin/spark-submit \
   * --master yarn-cluster \
   * --class com.aura.spark.streaming.JavaStreamingAnalysis \
   * $jar_file
   */
  public static void main(String[] args) {
    JavaStreamingAnalysis streaming = new JavaStreamingAnalysis();
    try {
      streaming.runAnalysis();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
