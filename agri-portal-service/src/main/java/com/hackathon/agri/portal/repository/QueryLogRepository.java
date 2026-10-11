package com.hackathon.agri.portal.repository;

import com.hackathon.agri.portal.model.QueryLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QueryLogRepository extends JpaRepository<QueryLog, Long> {

    List<QueryLog> findTop20ByOrderByCreatedTimeDesc();
}
