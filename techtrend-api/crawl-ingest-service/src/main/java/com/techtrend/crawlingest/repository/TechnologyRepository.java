package com.techtrend.crawlingest.repository;

import com.techtrend.crawlingest.entities.Technology;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Long> {
  @Query("select t.id from Technology t order by t.id")
  List<Long> findAllIds();
  /**
   * SELECT ... FOR UPDATE: concurrent webhooks on the same technology wait here instead of
   * losing increments or deadlocking. Must run inside a transaction.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Technology> findByCanonicalName(String canonicalName);

  /**
   * SELECT ... FOR UPDATE by id for the daily stats job, so it never overwrites a webhook's concurrent increment.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Technology> findWithLockById(Long id);
}
