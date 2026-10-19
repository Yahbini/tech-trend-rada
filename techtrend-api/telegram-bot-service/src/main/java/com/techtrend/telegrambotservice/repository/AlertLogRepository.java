package com.techtrend.telegrambotservice.repository;

import com.techtrend.telegrambotservice.entities.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertLogRepository extends JpaRepository<AlertLog, Long> {
}
