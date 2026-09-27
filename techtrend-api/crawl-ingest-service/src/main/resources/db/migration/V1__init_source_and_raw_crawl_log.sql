CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    weight DECIMAL(4,2),
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE source (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    crawl_interval_minutes INT,
    reliability_weight DECIMAL(4,2),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE technology (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    canonical_name VARCHAR(255) NOT NULL UNIQUE,
    category_id BIGINT,
    first_seen_at DATETIME,
    last_seen_at DATETIME,
    mention_count_total INT DEFAULT 0,
    is_new BOOLEAN DEFAULT TRUE,
    mention_count_last_7d INT DEFAULT 0,
    mention_count_prev_7d INT DEFAULT 0,
    mention_velocity DECIMAL(10,4) DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_technology_category FOREIGN KEY (category_id) REFERENCES category(id),
    INDEX idx_technology_last_seen_at (last_seen_at)
);

CREATE TABLE alias_map (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alias VARCHAR(255) NOT NULL UNIQUE,
    canonical_name VARCHAR(255) NOT NULL,
    INDEX idx_alias_map_canonical_name (canonical_name)
);

CREATE TABLE raw_crawl_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_id BIGINT NULL,
    external_id VARCHAR(255),
    raw_payload JSON,
    crawled_at DATETIME NULL,
    CONSTRAINT fk_raw_crawl_log_source FOREIGN KEY (source_id) REFERENCES source(id),
    INDEX idx_raw_crawl_log_source_crawled (source_id, crawled_at),
    INDEX idx_raw_crawl_log_external_id (external_id)
);

CREATE TABLE crawl_invalid_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    raw_crawl_log_id BIGINT NOT NULL,
    reason TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_civ_raw_crawl_log FOREIGN KEY (raw_crawl_log_id) REFERENCES raw_crawl_log(id),
    INDEX idx_civ_raw_crawl_log_id (raw_crawl_log_id)
);

CREATE TABLE unclassified_mention (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    raw_crawl_log_id BIGINT NOT NULL,
    raw_text TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_um_raw_crawl_log FOREIGN KEY (raw_crawl_log_id) REFERENCES raw_crawl_log(id),
    INDEX idx_um_raw_crawl_log_id (raw_crawl_log_id)
);

CREATE TABLE alert_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    canonical_name VARCHAR(255) NOT NULL,
    sent_at DATETIME NOT NULL,
    sent_date DATE AS (DATE(sent_at)) STORED,
    telegram_message_id VARCHAR(100),
    UNIQUE KEY uk_alert_log_name_date (canonical_name, sent_date)
);

INSERT INTO source (name, code, crawl_interval_minutes, reliability_weight) VALUES
  ('Hacker News',     'hackerNews',  30,    1.00),
    ('GitHub Trending', 'github',      360,   1.00),
    ('Reddit',          'reddit',      60,    1.00),
    ('dev.to',          'devto',       60,    1.00),
    ('TIOBE Index',     'tiobe',       43200, 1.00),
    ('Product Hunt',    'productHunt', 60,    1.00);
