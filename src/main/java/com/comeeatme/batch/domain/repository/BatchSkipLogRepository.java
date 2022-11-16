package com.comeeatme.batch.domain.repository;

import com.comeeatme.batch.domain.BatchSkipLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchSkipLogRepository extends JpaRepository<BatchSkipLog, Long> {
}
