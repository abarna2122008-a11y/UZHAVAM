package com.agri.ai.portal.controller;

import com.agri.ai.portal.dto.PestObservationRequest;
import com.agri.ai.portal.model.PestObservation;
import com.agri.ai.portal.repository.CommunityOfferRepository;
import com.agri.ai.portal.repository.ElderKnowledgeRepository;
import com.agri.ai.portal.repository.PestObservationRepository;
import com.agri.ai.portal.repository.SeasonOutcomeRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FarmInsightsControllerTest {

    @Test
    void pestSignalClearlyLabelsSharedWorkspaceCountsAsUnverified() {
        PestObservationRepository pestRepository = mock(PestObservationRepository.class);
        PestObservation observation = new PestObservation();
        observation.setCropName("Rice");
        observation.setDistrict("Thanjavur");
        observation.setSymptom("Leaf spots");
        observation.setObservedOn(LocalDate.now());
        when(pestRepository.findAllByOrderByObservedOnDescCreatedAtDesc()).thenReturn(List.of(observation));
        FarmInsightsController controller = new FarmInsightsController(
                mock(SeasonOutcomeRepository.class), mock(ElderKnowledgeRepository.class),
                pestRepository, mock(CommunityOfferRepository.class));

        var signal = controller.pestSignal();

        assertEquals(30, signal.get("windowDays"));
        assertTrue(signal.get("status").toString().contains("not a verified regional pest alert"));
    }

    @Test
    void preservesLocalPestObservationWithoutInventingDiagnosis() {
        PestObservationRepository pestRepository = mock(PestObservationRepository.class);
        when(pestRepository.save(org.mockito.ArgumentMatchers.any(PestObservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        FarmInsightsController controller = new FarmInsightsController(
                mock(SeasonOutcomeRepository.class), mock(ElderKnowledgeRepository.class),
                pestRepository, mock(CommunityOfferRepository.class));
        PestObservationRequest request = new PestObservationRequest();
        request.setCropName("Rice");
        request.setSymptom("Leaf spots");
        request.setDistrict("Thanjavur");
        request.setObservedOn(LocalDate.now());
        request.setObservations("Spots on two leaves");

        PestObservation saved = controller.savePest(request);

        assertEquals("Leaf spots", saved.getSymptom());
        assertEquals(false, saved.isResolved());
    }
}
