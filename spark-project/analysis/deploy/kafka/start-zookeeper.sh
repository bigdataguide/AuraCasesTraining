#!/usr/bin/env bash

export KAFAK_HOME=/home/bigdata/kafka_2.11-0.10.1.0

"$KAFAK_HOME"/bin/zookeeper-server-start.sh -daemon "$KAFAK_HOME"/config/zookeeper.properties