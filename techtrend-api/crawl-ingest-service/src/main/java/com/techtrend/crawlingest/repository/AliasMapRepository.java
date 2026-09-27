package com.techtrend.crawlingest.repository;

import com.techtrend.crawlingest.entities.AliasMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AliasMapRepository extends JpaRepository<AliasMap, Long> {
}
