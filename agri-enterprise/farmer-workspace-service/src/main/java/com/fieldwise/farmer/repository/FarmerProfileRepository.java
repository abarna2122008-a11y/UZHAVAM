package com.fieldwise.farmer.repository;

import com.fieldwise.farmer.model.FarmerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, Long> {
}
