package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.SoilResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SoilResultRepository extends JpaRepository<SoilResult, Long> {
    List<SoilResult> findAllByOrderByTestedOnDescCreatedAtDesc();
    List<SoilResult> findAllByTestTypeOrderByTestedOnAsc(String testType);
}
