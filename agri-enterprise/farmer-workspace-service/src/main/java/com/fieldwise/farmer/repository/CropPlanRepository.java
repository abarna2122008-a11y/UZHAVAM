package com.fieldwise.farmer.repository;

import com.fieldwise.farmer.model.CropPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropPlanRepository extends JpaRepository<CropPlan, Long> {

    List<CropPlan> findAllByOrderBySowingDateDesc();
}
