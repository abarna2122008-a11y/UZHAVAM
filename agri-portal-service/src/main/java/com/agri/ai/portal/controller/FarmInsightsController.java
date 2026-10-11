package com.agri.ai.portal.controller;

import com.agri.ai.portal.dto.CommunityOfferRequest;
import com.agri.ai.portal.dto.ElderKnowledgeRequest;
import com.agri.ai.portal.dto.PestObservationRequest;
import com.agri.ai.portal.dto.SeasonOutcomeRequest;
import com.agri.ai.portal.model.CommunityOffer;
import com.agri.ai.portal.model.ElderKnowledgeNote;
import com.agri.ai.portal.model.PestObservation;
import com.agri.ai.portal.model.SeasonOutcome;
import com.agri.ai.portal.repository.CommunityOfferRepository;
import com.agri.ai.portal.repository.ElderKnowledgeRepository;
import com.agri.ai.portal.repository.PestObservationRepository;
import com.agri.ai.portal.repository.SeasonOutcomeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/portal")
public class FarmInsightsController {
    private static final Set<String> OFFER_TYPES = Set.of("Transport", "Water turn", "Labour exchange", "Buyer / supplier");
    private final SeasonOutcomeRepository outcomes;
    private final ElderKnowledgeRepository knowledge;
    private final PestObservationRepository pests;
    private final CommunityOfferRepository community;

    public FarmInsightsController(SeasonOutcomeRepository outcomes, ElderKnowledgeRepository knowledge,
                                  PestObservationRepository pests, CommunityOfferRepository community) {
        this.outcomes = outcomes;
        this.knowledge = knowledge;
        this.pests = pests;
        this.community = community;
    }

    @GetMapping("/memory/outcomes")
    public List<SeasonOutcome> outcomes() {
        return outcomes.findAllByOrderByCompletedOnDesc();
    }

    @PostMapping("/memory/outcomes")
    @ResponseStatus(HttpStatus.CREATED)
    public SeasonOutcome saveOutcome(@Valid @RequestBody SeasonOutcomeRequest request) {
        if (request.getCompletedOn().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Season completion date cannot be in the future.");
        }
        if (!Set.of("kg", "quintal", "tonne", "bag", "piece").contains(request.getYieldUnit())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a supported yield unit.");
        }
        SeasonOutcome item = new SeasonOutcome();
        item.setCropName(request.getCropName().trim());
        item.setSeasonName(request.getSeasonName().trim());
        item.setCompletedOn(request.getCompletedOn());
        item.setCultivationCost(request.getCultivationCost());
        item.setYieldQuantity(request.getYieldQuantity());
        item.setYieldUnit(request.getYieldUnit());
        item.setSalesRevenue(request.getSalesRevenue());
        item.setNotes(request.getNotes() == null ? "" : request.getNotes().trim());
        item.setCreatedAt(Instant.now());
        return outcomes.save(item);
    }

    @DeleteMapping("/memory/outcomes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOutcome(@PathVariable long id) {
        if (!outcomes.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Season record not found.");
        outcomes.deleteById(id);
    }

    @GetMapping("/knowledge")
    public List<ElderKnowledgeNote> knowledge(@RequestParam(required = false) String search) {
        String query = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        return knowledge.findAllByOrderByCreatedAtDesc().stream()
                .filter(note -> query.isEmpty() || (note.getTitle() + " " + note.getCropTag() + " "
                        + note.getTopicTag() + " " + note.getNoteText()).toLowerCase(Locale.ROOT).contains(query))
                .toList();
    }

    @PostMapping("/knowledge")
    @ResponseStatus(HttpStatus.CREATED)
    public ElderKnowledgeNote saveKnowledge(@Valid @RequestBody ElderKnowledgeRequest request) {
        ElderKnowledgeNote note = new ElderKnowledgeNote();
        note.setTitle(request.getTitle().trim());
        note.setCropTag(request.getCropTag().trim());
        note.setTopicTag(request.getTopicTag().trim());
        note.setLanguage(request.getLanguage().trim());
        note.setContributor(request.getContributor().trim());
        note.setNoteText(request.getNoteText().trim());
        note.setVerified(false);
        note.setCreatedAt(Instant.now());
        return knowledge.save(note);
    }

    @DeleteMapping("/knowledge/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKnowledge(@PathVariable long id) {
        if (!knowledge.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Knowledge note not found.");
        knowledge.deleteById(id);
    }

    @GetMapping("/pestwatch")
    public List<PestObservation> pestWatch() {
        return pests.findAllByOrderByObservedOnDescCreatedAtDesc();
    }

    @PostMapping("/pestwatch")
    @ResponseStatus(HttpStatus.CREATED)
    public PestObservation savePest(@Valid @RequestBody PestObservationRequest request) {
        if (request.getObservedOn().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Observation date cannot be in the future.");
        }
        PestObservation item = new PestObservation();
        item.setCropName(request.getCropName().trim());
        item.setSymptom(request.getSymptom().trim());
        item.setDistrict(request.getDistrict().trim());
        item.setObservedOn(request.getObservedOn());
        item.setObservations(request.getObservations().trim());
        item.setActionTaken(request.getActionTaken() == null ? "" : request.getActionTaken().trim());
        item.setResolved(request.isResolved());
        item.setCreatedAt(Instant.now());
        return pests.save(item);
    }

    @PatchMapping("/pestwatch/{id}")
    public PestObservation updatePest(@PathVariable long id, @RequestParam boolean resolved,
                                      @RequestParam(required = false) String actionTaken) {
        PestObservation item = pests.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pest observation not found."));
        item.setResolved(resolved);
        if (actionTaken != null) item.setActionTaken(actionTaken.trim());
        return pests.save(item);
    }

    @DeleteMapping("/pestwatch/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePest(@PathVariable long id) {
        if (!pests.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pest observation not found.");
        pests.deleteById(id);
    }

    @GetMapping("/pestsignal")
    public Map<String, Object> pestSignal() {
        LocalDate since = LocalDate.now().minusDays(30);
        Map<String, Long> reports = pests.findAllByOrderByObservedOnDescCreatedAtDesc().stream()
                .filter(item -> !item.getObservedOn().isBefore(since))
                .collect(Collectors.groupingBy(item -> item.getDistrict() + " · " + item.getCropName()
                        + " · " + item.getSymptom(), Collectors.counting()));
        return Map.of("windowDays", 30, "reportCounts", reports,
                "status", "single-workspace observations only; not a verified regional pest alert");
    }

    @GetMapping("/community")
    public List<CommunityOffer> community(@RequestParam(required = false) String type,
                                         @RequestParam(required = false) String district) {
        String area = district == null ? "" : district.trim().toLowerCase(Locale.ROOT);
        return community.findAllByOrderByCreatedAtDesc().stream()
                .filter(offer -> "Available".equals(offer.getStatus()))
                .filter(offer -> type == null || type.isBlank() || offer.getOfferType().equalsIgnoreCase(type.trim()))
                .filter(offer -> area.isEmpty() || offer.getDistrict().toLowerCase(Locale.ROOT).contains(area))
                .toList();
    }

    @PostMapping("/community")
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityOffer createCommunityOffer(@Valid @RequestBody CommunityOfferRequest request) {
        if (!OFFER_TYPES.contains(request.getOfferType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a supported community offer type.");
        }
        if (request.getEndDate() != null && request.getAvailableDate() != null
                && request.getEndDate().isBefore(request.getAvailableDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be on or after the available date.");
        }
        CommunityOffer offer = new CommunityOffer();
        offer.setOfferType(request.getOfferType());
        offer.setTitle(request.getTitle().trim());
        offer.setContributor(request.getContributor().trim());
        offer.setDistrict(request.getDistrict().trim());
        offer.setDetails(request.getDetails().trim());
        offer.setQuantity(request.getQuantity());
        offer.setUnit(request.getUnit().trim());
        offer.setContactPhone(request.getContactPhone() == null ? "" : request.getContactPhone().trim());
        offer.setAvailableDate(request.getAvailableDate());
        offer.setEndDate(request.getEndDate());
        offer.setStatus("Available");
        offer.setCreatedAt(Instant.now());
        return community.save(offer);
    }

    @PatchMapping("/community/{id}/status")
    public CommunityOffer communityStatus(@PathVariable long id, @RequestParam String status) {
        CommunityOffer offer = community.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community offer not found."));
        if (!Set.of("Available", "Reserved", "Completed", "Cancelled").contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a valid offer status.");
        }
        offer.setStatus(status);
        return community.save(offer);
    }

    @DeleteMapping("/community/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommunityOffer(@PathVariable long id) {
        if (!community.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Community offer not found.");
        community.deleteById(id);
    }
}
