package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.CropPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropPlanRepository extends JpaRepository<CropPlan, Long> {
    List<CropPlan> findAllByOrderBySowingDateDesc();
}
