create table t_funnel_devicelog_tsv (
  xwho int,
  xwhen bigint,
  xwhat_id string,
  xwhat string,
  json string,
  day string
) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
LINES TERMINATED BY '\n'
STORED AS TEXTFILE;

LOAD DATA LOCAL INPATH '/home/bigdata/Desktop/funnel/20170102' OVERWRITE INTO TABLE
t_funnel_devicelog_tsv;