#!/usr/bin/env bash

export KAFKA_HOME=/home/bigdata/kafka_2.11-0.10.1.0

# 创建aura kafka topic, 一个分区一个副本
"$KAFKA_HOME"/bin/kafka-topics.sh --create --zookeeper bigdata:2181 --replication-factor 1 --partitions 1 --topic aura

# 查看创建好的topic
"$KAFKA_HOME"/bin/kafka-topics.sh --list --zookeeper bigdata:2181