SELECT
  ld_sum(xwho_state, 3)
FROM (
  SELECT
    ld_count(xwhen, cast(1 * 86400000 as bigint), xwhat_id, '10001,10004,10008') AS xwho_state
  FROM t_funnel_devicelog_tsv
  WHERE day >= '20170102' AND day <= '20170102'
  AND xwhat_id IN ('10004', '10001', '10008') GROUP BY xwho
) a;