package com.dev.springbootserver.repository;

import com.dev.springbootserver.model.ExitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExitLogRepository extends JpaRepository<ExitLog, Long> {
}
