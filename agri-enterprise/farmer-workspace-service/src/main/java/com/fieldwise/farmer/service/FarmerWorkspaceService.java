package com.fieldwise.farmer.service;

import com.fieldwise.farmer.dto.CropPlanRequest;
import com.fieldwise.farmer.dto.FarmTaskRequest;
import com.fieldwise.farmer.dto.FarmerProfileRequest;
import com.fieldwise.farmer.dto.MarketLink;
import com.fieldwise.farmer.dto.SchemeInfo;
import com.fieldwise.farmer.model.CropPlan;
import com.fieldwise.farmer.model.FarmTask;
import com.fieldwise.farmer.model.FarmerProfile;
import com.fieldwise.farmer.repository.CropPlanRepository;
import com.fieldwise.farmer.repository.FarmTaskRepository;
import com.fieldwise.farmer.repository.FarmerProfileRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class FarmerWorkspaceService {

    private final FarmerProfileRepository profileRepository;
    private final CropPlanRepository cropPlanRepository;
    private final FarmTaskRepository taskRepository;

    public FarmerWorkspaceService(
            FarmerProfileRepository profileRepository,
            CropPlanRepository cropPlanRepository,
            FarmTaskRepository taskRepository) {
        this.profileRepository = profileRepository;
        this.cropPlanRepository = cropPlanRepository;
        this.taskRepository = taskRepository;
    }

    public FarmerProfile saveProfile(FarmerProfileRequest request) {
        FarmerProfile profile = profileRepository.findById(1L).orElseGet(FarmerProfile::new);
        profile.setId(1L);
        profile.setFarmerName(request.getFarmerName().trim());
        profile.setFarmName(request.getFarmName().trim());
        profile.setVillage(request.getVillage().trim());
        profile.setDistrict(request.getDistrict().trim());
        profile.setState(request.getState().trim());
        profile.setSoilType(request.getSoilType().trim());
        profile.setLandAcres(request.getLandAcres());
        profile.setIrrigationSource(request.getIrrigationSource().trim());
        profile.setPreferredLanguage(request.getPreferredLanguage().trim());
        profile.setUpdatedAt(Instant.now());
        return profileRepository.save(profile);
    }

    public FarmerProfile getProfile() {
        return profileRepository.findById(1L).orElse(null);
    }

    public CropPlan addCropPlan(CropPlanRequest request) {
        if (request.getExpectedHarvestDate() != null
                && request.getExpectedHarvestDate().isBefore(request.getSowingDate())) {
            throw new IllegalArgumentException("Expected harvest date must be after the sowing date.");
        }
        CropPlan plan = new CropPlan(
                null,
                request.getCropName().trim(),
                request.getVariety() == null ? "" : request.getVariety().trim(),
                request.getSeason().trim(),
                request.getSowingDate(),
                request.getExpectedHarvestDate(),
                request.getAreaAcres(),
                request.getStatus().trim());
        return cropPlanRepository.save(plan);
    }

    public List<CropPlan> getCropPlans() {
        return cropPlanRepository.findAllByOrderBySowingDateDesc();
    }

    public void deleteCropPlan(long id) {
        if (!cropPlanRepository.existsById(id)) {
            throw new ResourceNotFoundException("Crop plan not found.");
        }
        cropPlanRepository.deleteById(id);
    }

    public FarmTask addTask(FarmTaskRequest request) {
        FarmTask task = new FarmTask(
                null,
                request.getTitle().trim(),
                request.getCropName() == null ? "" : request.getCropName().trim(),
                request.getTaskType().trim(),
                request.getDueDate(),
                false);
        return taskRepository.save(task);
    }

    public List<FarmTask> getTasks() {
        return taskRepository.findAllByOrderByDueDateAsc();
    }

    public FarmTask completeTask(long id, boolean completed) {
        FarmTask task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm task not found."));
        task.setCompleted(completed);
        return taskRepository.save(task);
    }

    public List<FarmTask> getAlerts() {
        LocalDate today = LocalDate.now();
        LocalDate upcoming = today.plusDays(7);
        return taskRepository.findAllByOrderByDueDateAsc().stream()
                .filter(task -> !task.isCompleted())
                .filter(task -> !task.getDueDate().isAfter(upcoming))
                .toList();
    }

    public List<SchemeInfo> getSchemes() {
        return List.of(
                new SchemeInfo(
                        "PM-KISAN",
                        "Income support scheme for eligible landholding farmer families. Check current enrollment and installment status on the official portal.",
                        "Eligibility, land-record requirements and exclusions are determined by the scheme authority.",
                        "https://pmkisan.gov.in/"),
                new SchemeInfo(
                        "Pradhan Mantri Fasal Bima Yojana",
                        "Crop insurance information, enrollment windows and claim guidance for notified crops and areas.",
                        "Coverage and deadlines depend on the notified crop, district and season; verify before applying.",
                        "https://pmfby.gov.in/"),
                new SchemeInfo(
                        "Kisan Credit Card",
                        "Explore institutional agricultural credit for eligible cultivation and allied activities.",
                        "Banks set eligibility, documentation and credit terms. Apply through an official bank channel.",
                        "https://www.myscheme.gov.in/"));
    }

    public List<MarketLink> getMarketLinks() {
        return List.of(
                new MarketLink(
                        "e-NAM",
                        "Official national agricultural market platform. Search current arrivals and prices by commodity and mandi.",
                        "https://www.enam.gov.in/web/dashboard/live_price"),
                new MarketLink(
                        "Agmarknet",
                        "Government market information portal for commodity-wise mandi prices and arrivals.",
                        "https://agmarknet.gov.in/"),
                new MarketLink(
                        "Farmers' produce marketplace",
                        "Use verified local FPOs and regulated market committees to compare buyer offers; avoid sharing OTPs or paying unverified agents.",
                        "https://www.enam.gov.in/"));
    }

    public FarmContextSummary getContext(String location) {
        FarmerProfile profile = getProfile();
        boolean sameLocation = profile != null && profile.getDistrict().equalsIgnoreCase(location.trim());
        List<CropPlan> crops = sameLocation ? getCropPlans() : List.of();
        List<FarmTask> tasks = sameLocation ? getAlerts() : List.of();
        String farmSummary = sameLocation
                ? profile.getFarmName() + ", " + profile.getVillage() + ", " + profile.getDistrict()
                + ", " + profile.getState() + "; soil " + profile.getSoilType()
                + "; " + profile.getLandAcres() + " acres; irrigation " + profile.getIrrigationSource() + "."
                : "No registered farm profile matches " + location + ".";
        String cropSummary = crops.isEmpty()
                ? "No crop plans are currently registered for this farm."
                : crops.stream().map(crop -> crop.getCropName() + " (" + crop.getSeason()
                        + ", " + crop.getAreaAcres() + " acres, status " + crop.getStatus() + ")")
                .collect(java.util.stream.Collectors.joining("; "));
        String tasksSummary = tasks.isEmpty()
                ? "No open field tasks due within the next seven days."
                : tasks.stream().map(task -> task.getTitle() + " due " + task.getDueDate())
                .collect(java.util.stream.Collectors.joining("; "));
        return new FarmContextSummary(
                location,
                farmSummary,
                cropSummary,
                tasksSummary,
                List.of("[FARMER-WORKSPACE] Farmer-entered farm profile, crop plan and task records."));
    }

    public record FarmContextSummary(
            String location,
            String farmSummary,
            String cropPlanSummary,
            String upcomingTasks,
            List<String> references) {
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
