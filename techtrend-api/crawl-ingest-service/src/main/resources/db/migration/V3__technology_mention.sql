CREATE TABLE technology_mention (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    technology_id BIGINT NOT NULL,
    raw_crawl_log_id BIGINT NOT NULL,
    mentioned_at DATETIME NOT NULL,
    CONSTRAINT fk_tm_technology FOREIGN KEY (technology_id) REFERENCES technology(id),
    CONSTRAINT fk_tm_raw_crawl_log FOREIGN KEY (raw_crawl_log_id) REFERENCES raw_crawl_log(id),
    INDEX idx_tm_technology_mentioned_at (technology_id, mentioned_at)
);