package com.fieldwise.portal.repository;

import com.fieldwise.portal.model.QueryLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QueryLogRepository extends JpaRepository<QueryLog, Long> {

    List<QueryLog> findAllByOrderByTimestampDesc();
}
