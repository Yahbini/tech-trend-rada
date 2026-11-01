package com.techtrend.crawlingest.repository;

import com.techtrend.crawlingest.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  @Query("""
        select c from Category c join CanonicalCategory cc on cc.category.id = c.id 
                where cc.canonicalName = :canonicalName
        """)
  Optional<Category> findByCanonicalName(@Param("canonicalName") String canonicalName);

  Optional<Category> findByCode(String code);
}
