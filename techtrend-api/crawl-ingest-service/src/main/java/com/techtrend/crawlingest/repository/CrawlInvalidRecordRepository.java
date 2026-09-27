package com.techtrend.crawlingest.repository;

import com.techtrend.crawlingest.entities.CrawlInvalidRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlInvalidRecordRepository extends JpaRepository<CrawlInvalidRecord, Long> {

}
