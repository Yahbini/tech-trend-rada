package com.techtrend.crawlingest.repository;

import com.techtrend.crawlingest.entities.RawCrawlLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawCrawlLogRepository extends JpaRepository<RawCrawlLog, Long> {

}
