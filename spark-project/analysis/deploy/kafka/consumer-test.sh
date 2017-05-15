#!/usr/bin/env bash

export KAFKA_HOME=/home/bigdata/kafka_2.11-0.10.1.0

"$KAFKA_HOME"/bin/kafka-console-consumer.sh --zookeeper bigdata:2181 --from-beginning --topic aura --max-messages 10