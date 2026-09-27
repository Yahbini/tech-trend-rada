CREATE KEYSPACE IF NOT EXISTS tech_trend_radar
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE tech_trend_radar;

CREATE TABLE IF NOT EXISTS tech_mentions_by_source (
   source text,
   time_bucket date,
   mention_id timeuuid,
   external_id text,
   raw_text text,
   url text,
   crawled_at timestamp,
   PRIMARY KEY ((source), time_bucket, mention_id)
) WITH CLUSTERING ORDER BY (time_bucket DESC, mention_id DESC);