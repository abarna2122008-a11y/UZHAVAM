package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.WeatherAlertEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherAlertRepository extends JpaRepository<WeatherAlertEvent, Long> {
    List<WeatherAlertEvent> findTop50ByOrderByReceivedAtDesc();
    boolean existsByAlertKey(String alertKey);
}
