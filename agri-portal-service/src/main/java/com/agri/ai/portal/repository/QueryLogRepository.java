package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.QueryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryLogRepository extends JpaRepository<QueryLog, Long> {

    List<QueryLog> findTop30ByOrderByTimestampDesc();
}
