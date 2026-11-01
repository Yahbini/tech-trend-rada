-- 1. alert_log: key by technology_id, one alert per technology per UTC day.
alter table alert_log add column technology_id bigint not null after id,
add constraint fk_alert_log_technology foreign key (technology_id) references
 technology(id), drop index uk_alert_log_name_date, add unique key uk_alert_log_tech_date (technology_id, sent_date);

-- 2. tech_candidate + tech_candidate_mention: new-technology detection layer.
CREATE TABLE tech_candidate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    mention_count INT NOT NULL DEFAULT 0, -- total
    source_count INT NOT NULL DEFAULT 0, -- total
    score DECIMAL(10,2) NOT NULL DEFAULT 0,
    first_seen_at DATETIME NOT NULL,
    last_seen_at DATETIME NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    -- NULL = not yet reported to Telegram; set once so a candidate is announced only one time.
    notified_at DATETIME NULL,
    reviewed_at DATETIME NULL,
    UNIQUE KEY uk_tech_candidate_name (name),
    INDEX idx_tech_candidate_status_score (status, score)
);

CREATE TABLE tech_candidate_mention (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    candidate_id BIGINT NOT NULL,
    unclassified_mention_id BIGINT NOT NULL,
    source_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tcm_candidate FOREIGN KEY (candidate_id) REFERENCES tech_candidate(id),
    CONSTRAINT fk_tcm_unclassified_mention FOREIGN KEY (unclassified_mention_id) REFERENCES unclassified_mention(id),
    CONSTRAINT fk_tcm_source FOREIGN KEY (source_id) REFERENCES source(id),
    -- One mention counts once per candidate; makes re-processing/backfill idempotent.
    UNIQUE KEY uk_tcm_candidate_mention (candidate_id, unclassified_mention_id),
    INDEX idx_tcm_unclassified_mention_id (unclassified_mention_id),
    INDEX idx_tcm_source_id (source_id)
);

-- 3. muted_technologies: /mute from Telegram, muted_until NULL = forever.
CREATE TABLE muted_technologies (
    technology_id BIGINT PRIMARY KEY,
    muted_at DATETIME NOT NULL,
    muted_until DATETIME NULL,
    CONSTRAINT fk_muted_technologies_technology FOREIGN KEY (technology_id) REFERENCES technology(id)
);

-- 4. category.weight: tiered weights for trend scoring (column exists since V1).
UPDATE category
SET weight = CASE code
    WHEN 'ai-tool'   THEN 1.00
    WHEN 'language'  THEN 0.90
    WHEN 'framework' THEN 0.90
    WHEN 'database'  THEN 0.85
    WHEN 'devops'    THEN 0.70
    WHEN 'cloud'     THEN 0.70
    WHEN 'messaging' THEN 0.70
    WHEN 'testing'   THEN 0.60
    WHEN 'tool'      THEN 0.60
    ELSE 0.50
END;

-- NULL weight would make the whole score NULL; new categories default to the lowest tier.
ALTER TABLE category MODIFY weight DECIMAL(4,2) NOT NULL DEFAULT 0.50;