#!/usr/bin/env bash

tables=("sparkcore_dimension_data" \
"sparkcore_content_data" \
"sparkcore_content_detail" \
"mllib_gender_data" \
"mllib_channel_data" \
"streaming_dimension_data" \
"streaming_content_data" \
"streaming_content_detail")

for t in ${tables[@]}
do
    echo "truncate table aura.$t"
    mysql -h bigdata -u bigdata -pbigdata -e "truncate table aura.$t;"
done