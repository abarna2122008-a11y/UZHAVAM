package com.hackathon.agri.portal.service;

import com.hackathon.agri.portal.dto.AnalyticsSummary;
import com.hackathon.agri.portal.model.QueryLog;
import com.hackathon.agri.portal.repository.QueryLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortalServiceTest {

    @Test
    void buildsAnalyticsFromSavedInteractions() {
        QueryLogRepository repository = mock(QueryLogRepository.class);
        when(repository.findAll()).thenReturn(List.of(
                new QueryLog("Rice pests?", "Thanjavur", "English", "pest management", "Advice"),
                new QueryLog("நெல்?", "Thanjavur", "Tamil", "crop selection and cultivation", "ஆலோசனை"),
                new QueryLog("More rice pests?", "Pune", "English", "pest management", "Advice")));
        PortalService service = new PortalService(mock(RestTemplate.class), repository, "http://localhost:8081");

        AnalyticsSummary analytics = service.getAnalytics();

        assertEquals(3, analytics.getTotalQueries());
        assertEquals(2, analytics.getCategoryCount());
        assertEquals(2, analytics.getLanguageCount());
        assertEquals(2, analytics.getDistrictCount());
        assertEquals("pest management", analytics.getCategories().get(0).getLabel());
        assertEquals(2, analytics.getCategories().get(0).getCount());
        assertEquals(67, analytics.getCategories().get(0).getPercentage());
        assertEquals("English", analytics.getLanguages().get(0).getLabel());
        assertEquals(2, analytics.getLanguages().get(0).getCount());
    }
}
