package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.HarvestSale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HarvestSaleRepository extends JpaRepository<HarvestSale, Long> {
    List<HarvestSale> findAllByOrderBySaleDateDescCreatedAtDesc();
}
