#!/usr/bin/env bash

export KAFKA_HOME=/home/bigdata/kafka_2.11-0.10.1.0

"$KAFKA_HOME"/bin/kafka-server-start.sh -daemon "$KAFKA_HOME"/config/server.properties