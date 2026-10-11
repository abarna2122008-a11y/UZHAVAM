package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.FarmTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmTaskRepository extends JpaRepository<FarmTask, Long> {
    List<FarmTask> findAllByOrderByDueDateAsc();
    long countByCompletedFalse();
}
