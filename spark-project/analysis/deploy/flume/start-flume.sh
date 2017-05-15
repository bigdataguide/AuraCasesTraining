#!/usr/bin/env bash

export FLUME_HOME=/home/bigdata/apache-flume-1.7.0-bin

"$FLUME_HOME"/bin/flume-ng agent \
    --conf "$FLUME_HOME"/conf \
    --conf-file "$FLUME_HOME"/conf/aura-kafka.properties \
    --name logAgent \
    -Dflume.root.logger=INFO,console &