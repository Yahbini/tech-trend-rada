package com.techtrend.essinkconsumerservice.repository;

import com.techtrend.essinkconsumerservice.dto.TechnologyLookup;
import com.techtrend.essinkconsumerservice.entities.Technology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Long> {
  @Query("""
        select new com.techtrend.essinkconsumerservice.dto.TechnologyLookup(t.canonicalName, t.categoryId)
        from Technology t
        where t.id = :id
        """)
  Optional<TechnologyLookup> findLookupById(@Param("id") Long id);
}
