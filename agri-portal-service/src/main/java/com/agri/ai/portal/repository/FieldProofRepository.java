package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.FieldProofRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldProofRepository extends JpaRepository<FieldProofRecord, Long> {
    List<FieldProofRecord> findAllByOrderByIncidentDateDescCreatedAtDesc();
}
