package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.FarmerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, Long> {
}
