package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.PestObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PestObservationRepository extends JpaRepository<PestObservation, Long> {
    List<PestObservation> findAllByOrderByObservedOnDescCreatedAtDesc();
    List<PestObservation> findAllByOrderByCreatedAtDesc();
}
