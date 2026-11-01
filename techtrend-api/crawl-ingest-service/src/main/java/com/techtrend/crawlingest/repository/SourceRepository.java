package com.techtrend.crawlingest.repository;

import com.techtrend.crawlingest.entities.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {
  Optional<Source> findByCode(String code);
}
