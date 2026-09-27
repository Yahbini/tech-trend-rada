package com.techtrend.crawlingest.repository;

import com.techtrend.crawlingest.entities.UnclassifiedMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnclassifiedMentionRepository extends JpaRepository<UnclassifiedMention, Long> {
}
