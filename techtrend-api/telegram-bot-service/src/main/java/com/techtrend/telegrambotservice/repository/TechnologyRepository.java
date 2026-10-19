package com.techtrend.telegrambotservice.repository;

import com.techtrend.telegrambotservice.dto.TechnologyLookup;
import com.techtrend.telegrambotservice.entities.Technology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Long> {
  @Query("""
             select new com.techtrend.telegrambotservice.dto.TechnologyLookup(t.canonicalName, c.code)
             from Technology t left join t.category c
             where t.id = :id
        """)
  Optional<TechnologyLookup> findLookupById(@Param("id") Long id);
}
