package com.agri.ai.portal.controller;

import com.agri.ai.portal.dto.WeatherAlertMessage;
import com.agri.ai.portal.model.WeatherAlertEvent;
import com.agri.ai.portal.repository.WeatherAlertRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Validated
@RestController
@RequestMapping("/api/portal/weather-alerts")
public class WeatherAlertController {
    private static final Logger log = LoggerFactory.getLogger(WeatherAlertController.class);
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper objectMapper;
    private final WeatherAlertRepository alerts;
    private final String topic;

    public WeatherAlertController(KafkaTemplate<String, String> kafka, ObjectMapper objectMapper,
                                  WeatherAlertRepository alerts,
                                  @Value("${app.weather-alert-topic:uzhavam.weather.alerts}") String topic) {
        this.kafka = kafka;
        this.objectMapper = objectMapper;
        this.alerts = alerts;
        this.topic = topic;
    }

    @PostMapping
    public Map<String, String> publish(@Valid @RequestBody WeatherAlertMessage message) {
        if (alerts.existsByAlertKey(message.getAlertKey())) {
            return Map.of("status", "already-queued", "alertKey", message.getAlertKey());
        }
        try {
            String payload = objectMapper.writeValueAsString(message);
            RecordMetadata metadata = kafka.send(topic, message.getAlertKey(), payload)
                    .get(5, TimeUnit.SECONDS).getRecordMetadata();
            return Map.of("status", "queued", "alertKey", message.getAlertKey(),
                    "topic", metadata.topic(), "partition", String.valueOf(metadata.partition()));
        } catch (Exception exception) {
            log.error("Could not publish forecast alert {} to Kafka.", message.getAlertKey(), exception);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Weather alert could not be queued. Check that the Kafka alert broker is running.", exception);
        }
    }

    @GetMapping
    public List<WeatherAlertEvent> recent() {
        return alerts.findTop50ByOrderByReceivedAtDesc();
    }

    @KafkaListener(topics = "${app.weather-alert-topic:uzhavam.weather.alerts}", groupId = "uzhavam-weather-alerts")
    public void consume(String payload) {
        try {
            WeatherAlertMessage message = objectMapper.readValue(payload, WeatherAlertMessage.class);
            if (alerts.existsByAlertKey(message.getAlertKey())) return;
            WeatherAlertEvent event = new WeatherAlertEvent();
            event.setAlertKey(message.getAlertKey());
            event.setDistrict(message.getDistrict());
            event.setHeadline(message.getHeadline());
            event.setRiskDetails(message.getRiskDetails());
            event.setForecastDate(message.getForecastDate());
            event.setSource(message.getSource());
            event.setReceivedAt(Instant.now());
            alerts.save(event);
        } catch (Exception exception) {
            log.error("Could not consume weather alert event from Kafka.", exception);
            throw new IllegalStateException("Weather alert event could not be stored.", exception);
        }
    }
}
