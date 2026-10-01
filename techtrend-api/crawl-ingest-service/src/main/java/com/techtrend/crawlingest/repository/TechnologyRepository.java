package com.techtrend.crawlingest.repository;

import com.techtrend.crawlingest.entities.Technology;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Long> {
  /**
   * SELECT ... FOR UPDATE: concurrent webhooks on the same technology wait here instead of
   * losing increments or deadlocking. Must run inside a transaction.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Technology> findByCanonicalName(String canonicalName);
}
