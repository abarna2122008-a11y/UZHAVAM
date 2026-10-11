package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.SeasonOutcome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeasonOutcomeRepository extends JpaRepository<SeasonOutcome, Long> {
    List<SeasonOutcome> findAllByOrderByCompletedOnDesc();
}
