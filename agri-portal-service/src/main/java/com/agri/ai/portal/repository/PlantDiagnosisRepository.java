package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.PlantDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantDiagnosisRepository extends JpaRepository<PlantDiagnosis, Long> {

    List<PlantDiagnosis> findTop20ByOrderByCreatedAtDesc();
}
