package com.aura.spark.streaming;

import com.aura.util.AuraConfig;
import com.aura.util.LogUtil;
import com.google.common.util.concurrent.RateLimiter;
import com.typesafe.config.Config;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Properties;
import java.util.stream.Stream;

public class JavaKafkaLogProducer {

    private RateLimiter limiter;
    private String topic;
    private Producer<String, String> producer;

    public JavaKafkaLogProducer(int maxRatePerSecond) {
        topic = AuraConfig.getRoot().getString("streaming.topic");
        limiter = RateLimiter.create(maxRatePerSecond);
        producer = createProducer();
    }

    private Producer<String, String> createProducer() {
        Config config = AuraConfig.getKafka();
        Properties props = new Properties();
        props.put("metadata.broker.list", config.getString("metadata.broker.list"));
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        return new Producer<String, String>(new ProducerConfig(props));
    }

    public void replayLog(String logFile) {
        try (Stream<String> stream = Files.lines(Paths.get(logFile))) {
            Iterator<String> itr = stream.iterator();
            while (itr.hasNext()) {
                limiter.acquire();
                String json = LogUtil.resetSecond(itr.next(), System.currentTimeMillis()/1000);
                System.out.println(json);
                producer.send(new KeyedMessage<String, String>(topic, json));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: JavaKafkaLogProducer <maxRatePerSecond> <logFilePath>");
            System.exit(-1);
        }
        int maxRate = Integer.parseInt(args[0]);
        JavaKafkaLogProducer producer = new JavaKafkaLogProducer(maxRate);
        producer.replayLog(args[1]);
    }

}
