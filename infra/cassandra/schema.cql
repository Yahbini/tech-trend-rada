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

-- Mention history per technology; month bucket (yyyy-MM UTC) keeps partitions bounded.
-- Natural clustering key: replaying a message overwrites the same row instead of duplicating it.
CREATE TABLE IF NOT EXISTS tech_mentions_by_tech (
                                                     technology_id bigint,
                                                     month text,
                                                     crawled_at timestamp,
                                                     source text,
                                                     external_id text,
                                                     url text,
                                                     raw_text text,
                                                     PRIMARY KEY ((technology_id, month), crawled_at, source, external_id)
) WITH CLUSTERING ORDER BY (crawled_at DESC, source ASC, external_id ASC);