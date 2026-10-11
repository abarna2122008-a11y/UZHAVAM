package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.SafeSprayRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SafeSprayRepository extends JpaRepository<SafeSprayRecord, Long> {
    List<SafeSprayRecord> findAllByOrderByAppliedDateDesc();
}
