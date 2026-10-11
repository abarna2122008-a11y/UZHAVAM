package com.fieldwise.farmer.repository;

import com.fieldwise.farmer.model.FarmTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmTaskRepository extends JpaRepository<FarmTask, Long> {

    List<FarmTask> findAllByOrderByDueDateAsc();
}
