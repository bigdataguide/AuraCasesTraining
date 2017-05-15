#!/usr/bin/env bash

export FLUME_HOME=/home/bigdata/apache-flume-1.7.0-bin
logfile="/home/bigdata/Aura/data/logs/aura.log"

"$FLUME_HOME"/bin/flume-ng avro-client \
  --conf "$FLUME_HOME"/conf \
  -H bigdata \
  -p 41414 \
  -F $logfile