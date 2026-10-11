package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.ReLoopListing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReLoopListingRepository extends JpaRepository<ReLoopListing, Long> {
    List<ReLoopListing> findAllByOrderByCreatedAtDesc();
}
