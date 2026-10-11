package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.CommunityOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityOfferRepository extends JpaRepository<CommunityOffer, Long> {
    List<CommunityOffer> findAllByOrderByCreatedAtDesc();
}
