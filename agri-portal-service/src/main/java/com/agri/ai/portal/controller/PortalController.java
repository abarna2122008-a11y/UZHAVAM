package com.agri.ai.portal.controller;

import com.agri.ai.portal.dto.CropPlanRequest;
import com.agri.ai.portal.dto.FarmTaskRequest;
import com.agri.ai.portal.dto.FarmerProfileRequest;
import com.agri.ai.portal.dto.InteractionLogRequest;
import com.agri.ai.portal.dto.PlantDiagnosisRequest;
import com.agri.ai.portal.dto.FarmTransactionRequest;
import com.agri.ai.portal.dto.HarvestSaleRequest;
import com.agri.ai.portal.dto.HarvestSettlementRequest;
import com.agri.ai.portal.model.CropPlan;
import com.agri.ai.portal.model.FarmTask;
import com.agri.ai.portal.model.FarmerProfile;
import com.agri.ai.portal.model.FarmTransaction;
import com.agri.ai.portal.model.HarvestSale;
import com.agri.ai.portal.model.QueryLog;
import com.agri.ai.portal.model.PlantDiagnosis;
import com.agri.ai.portal.repository.CropPlanRepository;
import com.agri.ai.portal.repository.FarmTaskRepository;
import com.agri.ai.portal.repository.FarmerProfileRepository;
import com.agri.ai.portal.repository.FarmTransactionRepository;
import com.agri.ai.portal.repository.HarvestSaleRepository;
import com.agri.ai.portal.repository.QueryLogRepository;
import com.agri.ai.portal.repository.PlantDiagnosisRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/portal")
public class PortalController {

    private final QueryLogRepository queryLogs;
    private final PlantDiagnosisRepository diagnoses;
    private final FarmerProfileRepository profiles;
    private final CropPlanRepository crops;
    private final FarmTaskRepository tasks;
    private final FarmTransactionRepository transactions;
    private final HarvestSaleRepository harvestSales;

    public PortalController(
            QueryLogRepository queryLogs,
            FarmerProfileRepository profiles,
            CropPlanRepository crops,
            FarmTaskRepository tasks,
            FarmTransactionRepository transactions,
            PlantDiagnosisRepository diagnoses,
            HarvestSaleRepository harvestSales) {
        this.queryLogs = queryLogs;
        this.profiles = profiles;
        this.crops = crops;
        this.tasks = tasks;
        this.transactions = transactions;
        this.diagnoses = diagnoses;
        this.harvestSales = harvestSales;
    }

    @PostMapping("/diagnoses")
    @ResponseStatus(HttpStatus.CREATED)
    public PlantDiagnosis savePlantDiagnosis(@Valid @RequestBody PlantDiagnosisRequest request) {
        PlantDiagnosis diagnosis = new PlantDiagnosis();
        diagnosis.setCropName(request.getCropName().trim());
        diagnosis.setDistrict(request.getDistrict().trim());
        diagnosis.setLanguage(request.getLanguage().trim());
        diagnosis.setCropIdentified(request.getCropIdentified().trim());
        diagnosis.setLikelyIssue(request.getLikelyIssue().trim());
        diagnosis.setConfidence(request.getConfidence().trim());
        diagnosis.setSeverity(request.getSeverity().trim());
        diagnosis.setObservations(request.getObservations().trim());
        diagnosis.setRecommendations(request.getRecommendations().trim());
        diagnosis.setImageName(request.getImageName().trim());
        diagnosis.setCreatedAt(Instant.now());
        diagnosis.setSourceReferences(request.getSourceReferences());
        return diagnoses.save(diagnosis);
    }

    @GetMapping("/diagnoses")
    public List<PlantDiagnosis> plantDiagnosisHistory() {
        return diagnoses.findTop20ByOrderByCreatedAtDesc();
    }

    @DeleteMapping("/diagnoses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlantDiagnosis(@PathVariable long id) {
        if (!diagnoses.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant check not found.");
        }
        diagnoses.deleteById(id);
    }

    @PostMapping("/log")
    @ResponseStatus(HttpStatus.CREATED)
    public QueryLog logInteraction(@Valid @RequestBody InteractionLogRequest request) {
        return queryLogs.save(new QueryLog(
                request.getFarmerQuery().trim(),
                request.getDetectedLanguage().trim(),
                request.getAiResponse(),
                Instant.now(),
                request.getSourceReferences()));
    }

    @GetMapping("/history")
    public List<QueryLog> history() {
        return queryLogs.findTop30ByOrderByTimestampDesc();
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        List<FarmTask> upcoming = tasks.findAllByOrderByDueDateAsc().stream()
                .filter(task -> !task.isCompleted())
                .filter(task -> !task.getDueDate().isBefore(LocalDate.now()))
                .limit(5)
                .toList();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("profile", profiles.findById(1L).orElse(null));
        result.put("crops", crops.findAllByOrderBySowingDateDesc());
        result.put("tasks", upcoming);
        result.put("queryHistory", queryLogs.findTop30ByOrderByTimestampDesc());
        result.put("totalQueries", queryLogs.count());
        result.put("activeCrops", crops.count());
        result.put("openTasks", tasks.countByCompletedFalse());
        result.put("transactions", transactions.findTop30ByOrderByEntryDateDesc());
        result.put("harvestSales", harvestSales.findAllByOrderBySaleDateDescCreatedAtDesc());
        return result;
    }

    @GetMapping("/harvest-sales")
    public List<HarvestSale> harvestSales() {
        return harvestSales.findAllByOrderBySaleDateDescCreatedAtDesc();
    }

    @PostMapping("/harvest-sales")
    @ResponseStatus(HttpStatus.CREATED)
    public HarvestSale recordHarvestSale(@Valid @RequestBody HarvestSaleRequest request) {
        HarvestSale sale = new HarvestSale();
        sale.setCropName(request.getCropName().trim());
        sale.setBuyerName(request.getBuyerName().trim());
        sale.setMarketName(request.getMarketName() == null ? "" : request.getMarketName().trim());
        sale.setAgreedQuantityQuintals(request.getAgreedQuantityQuintals());
        sale.setAgreedPricePerQuintal(request.getAgreedPricePerQuintal());
        sale.setTransportCost(request.getTransportCost());
        sale.setDeductions(request.getDeductions());
        sale.setAmountPaid(java.math.BigDecimal.ZERO);
        sale.setSaleDate(request.getSaleDate());
        sale.setAgreementNotes(request.getAgreementNotes() == null ? "" : request.getAgreementNotes().trim());
        sale.setCreatedAt(Instant.now());
        return harvestSales.save(sale);
    }

    @PatchMapping("/harvest-sales/{id}/settlement")
    public HarvestSale confirmHarvestSettlement(
            @PathVariable long id,
            @Valid @RequestBody HarvestSettlementRequest request) {
        HarvestSale sale = harvestSales.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Harvest sale not found."));
        java.math.BigDecimal netProceeds = request.getWeighedQuantityQuintals()
                .multiply(sale.getAgreedPricePerQuintal())
                .subtract(sale.getTransportCost())
                .subtract(request.getDeductions());
        if (netProceeds.signum() < 0 || request.getAmountPaid().compareTo(netProceeds) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment cannot exceed actual net proceeds.");
        }
        sale.setWeighedQuantityQuintals(request.getWeighedQuantityQuintals());
        sale.setDeductions(request.getDeductions());
        sale.setAmountPaid(request.getAmountPaid());
        return harvestSales.save(sale);
    }

    @DeleteMapping("/harvest-sales/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHarvestSale(@PathVariable long id) {
        if (!harvestSales.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Harvest sale not found.");
        }
        harvestSales.deleteById(id);
    }

    @PostMapping("/demo-data")
    public Map<String, Object> addDemoData() {
        if (profiles.existsById(1L)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A farm profile already exists.");
        }
        FarmerProfile profile = new FarmerProfile();
        profile.setId(1L);
        profile.setFarmerName("Anitha");
        profile.setFarmName("Green Field");
        profile.setVillage("Thiruvaiyaru");
        profile.setDistrict("Thanjavur");
        profile.setState("Tamil Nadu");
        profile.setSoilType("Alluvial");
        profile.setLandAcres(new java.math.BigDecimal("3.50"));
        profile.setIrrigationSource("Canal");
        profile.setPreferredLanguage("Tamil");
        profile.setUpdatedAt(Instant.now());
        profiles.save(profile);

        CropPlan rice = new CropPlan();
        rice.setCropName("Rice");
        rice.setVariety("ADT Rice (demo)");
        rice.setSeason("Samba");
        rice.setSowingDate(LocalDate.now().minusDays(24));
        rice.setHarvestDate(LocalDate.now().plusDays(96));
        rice.setAreaAcres(new java.math.BigDecimal("2.50"));
        rice.setStatus("Growing");
        crops.save(rice);

        CropPlan banana = new CropPlan();
        banana.setCropName("Banana");
        banana.setVariety("Poovan");
        banana.setSeason("Year-round");
        banana.setSowingDate(LocalDate.now().minusDays(68));
        banana.setHarvestDate(LocalDate.now().plusDays(210));
        banana.setAreaAcres(new java.math.BigDecimal("1.00"));
        banana.setStatus("Growing");
        crops.save(banana);

        FarmTask water = new FarmTask();
        water.setTitle("Check water level in rice field");
        water.setCropName("Rice");
        water.setTaskType("Irrigation");
        water.setDueDate(LocalDate.now().plusDays(1));
        water.setCompleted(false);
        tasks.save(water);

        FarmTask soil = new FarmTask();
        soil.setTitle("Collect soil sample for testing");
        soil.setCropName("Banana");
        soil.setTaskType("Soil health");
        soil.setDueDate(LocalDate.now().plusDays(3));
        soil.setCompleted(false);
        tasks.save(soil);

        return Map.of("message", "Sample farm workspace created.", "farmer", profile.getFarmerName());
    }

    @GetMapping("/profile")
    public FarmerProfile profile() {
        return profiles.findById(1L).orElse(null);
    }

    @PutMapping("/profile")
    public FarmerProfile saveProfile(@Valid @RequestBody FarmerProfileRequest request) {
        FarmerProfile profile = profiles.findById(1L).orElseGet(FarmerProfile::new);
        profile.setId(1L);
        profile.setFarmerName(request.getFarmerName().trim());
        profile.setFarmName(request.getFarmName() == null || request.getFarmName().isBlank()
                ? "My farm" : request.getFarmName().trim());
        profile.setVillage(request.getVillage().trim());
        profile.setDistrict(request.getDistrict().trim());
        profile.setState(request.getState().trim());
        profile.setSoilType(request.getSoilType() == null || request.getSoilType().isBlank()
                ? "Not provided" : request.getSoilType().trim());
        profile.setLandAcres(request.getLandAcres());
        profile.setIrrigationSource(request.getIrrigationSource().trim());
        profile.setPreferredLanguage(request.getPreferredLanguage().trim());
        profile.setUpdatedAt(Instant.now());
        return profiles.save(profile);
    }

    @GetMapping("/crops")
    public List<CropPlan> cropPlans() {
        return crops.findAllByOrderBySowingDateDesc();
    }

    @PostMapping("/crops")
    @ResponseStatus(HttpStatus.CREATED)
    public CropPlan addCropPlan(@Valid @RequestBody CropPlanRequest request) {
        if (request.getHarvestDate() != null && request.getHarvestDate().isBefore(request.getSowingDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Harvest date must be after sowing date.");
        }
        CropPlan plan = new CropPlan();
        plan.setCropName(request.getCropName().trim());
        plan.setVariety(request.getVariety() == null ? "" : request.getVariety().trim());
        plan.setSeason(request.getSeason().trim());
        plan.setSowingDate(request.getSowingDate());
        plan.setHarvestDate(request.getHarvestDate());
        plan.setAreaAcres(request.getAreaAcres());
        plan.setStatus(request.getStatus().trim());
        return crops.save(plan);
    }

    @DeleteMapping("/crops/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCropPlan(@PathVariable long id) {
        if (!crops.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Crop plan not found.");
        }
        crops.deleteById(id);
    }

    @GetMapping("/tasks")
    public List<FarmTask> farmTasks() {
        return tasks.findAllByOrderByDueDateAsc();
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public FarmTask addTask(@Valid @RequestBody FarmTaskRequest request) {
        FarmTask task = new FarmTask();
        task.setTitle(request.getTitle().trim());
        task.setCropName(request.getCropName() == null ? "" : request.getCropName().trim());
        task.setTaskType(request.getTaskType().trim());
        task.setDueDate(request.getDueDate());
        task.setCompleted(false);
        return tasks.save(task);
    }

    @PatchMapping("/tasks/{id}")
    public FarmTask updateTask(@PathVariable long id, @RequestParam boolean completed) {
        FarmTask task = tasks.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm task not found."));
        task.setCompleted(completed);
        return tasks.save(task);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable long id) {
        if (!tasks.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm task not found.");
        }
        tasks.deleteById(id);
    }

    @GetMapping("/alerts")
    public List<FarmTask> alerts() {
        LocalDate today = LocalDate.now();
        return tasks.findAllByOrderByDueDateAsc().stream()
                .filter(task -> !task.isCompleted())
                .filter(task -> !task.getDueDate().isBefore(today))
                .filter(task -> !task.getDueDate().isAfter(today.plusDays(7)))
                .toList();
    }

    @GetMapping("/schemes")
    public List<Map<String, String>> schemes() {
        return List.of(
                Map.of(
                        "name", "PM-KISAN",
                        "summary", "A Government of India income-support scheme for eligible farmer families.",
                        "whyItMatters", "It may provide direct financial support that farmers can use toward farm and household needs.",
                        "whatToCheck", "Check current eligibility, beneficiary status, bank details, and application guidance on the official portal. Do not share OTPs with callers.",
                        "url", "https://pmkisan.gov.in/"),
                Map.of(
                        "name", "Pradhan Mantri Fasal Bima Yojana",
                        "summary", "Government crop insurance information for notified crops, seasons, and areas.",
                        "whyItMatters", "Insurance may help eligible farmers manage certain crop losses covered by the policy.",
                        "whatToCheck", "Confirm your crop and area are notified, the current enrollment deadline, premium, exclusions, and claim steps with the official portal or local agriculture office.",
                        "url", "https://pmfby.gov.in/"),
                Map.of(
                        "name", "Kisan Credit Card",
                        "summary", "A bank credit facility intended to support eligible farmers' seasonal agricultural expenses.",
                        "whyItMatters", "A sanctioned credit limit may help meet approved cultivation and allied-activity costs.",
                        "whatToCheck", "Ask a participating bank about eligibility, interest, repayment dates, required documents, and terms. The bank decides approval.",
                        "url", "https://www.myscheme.gov.in/"));
    }

    @GetMapping("/markets")
    public List<Map<String, String>> markets() {
        return List.of(
                Map.of(
                        "name", "e-NAM live prices",
                        "summary", "The National Agriculture Market (e-NAM) connects participating agricultural markets and publishes market information.",
                        "whyItMatters", "Compare reported prices and arrivals across participating mandis before deciding where to ask about selling.",
                        "whatToCheck", "Choose the right commodity, market, grade, and date. Prices can change and may not equal the final price offered to you.",
                        "url", "https://www.enam.gov.in/web/dashboard/live_price"),
                Map.of(
                        "name", "Agmarknet",
                        "summary", "Government agricultural marketing information with commodity-wise market arrivals and price reports.",
                        "whyItMatters", "Historical and current reports can help you understand market movement in nearby mandis.",
                        "whatToCheck", "Filter by state, district, market, commodity, and date. Confirm the latest details with the mandi before transporting produce.",
                        "url", "https://agmarknet.gov.in/"));
    }

    @GetMapping("/transactions")
    public List<FarmTransaction> transactions() {
        return transactions.findTop30ByOrderByEntryDateDesc();
    }

    @PostMapping("/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public FarmTransaction addTransaction(@Valid @RequestBody FarmTransactionRequest request) {
        String entryType = request.getEntryType().trim().toLowerCase(java.util.Locale.ROOT);
        if (!entryType.equals("expense") && !entryType.equals("income")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entry type must be expense or income.");
        }
        FarmTransaction transaction = new FarmTransaction();
        transaction.setEntryType(entryType);
        transaction.setCategory(request.getCategory().trim());
        transaction.setCropName(request.getCropName() == null ? "" : request.getCropName().trim());
        transaction.setAmount(request.getAmount());
        transaction.setEntryDate(request.getEntryDate());
        transaction.setNotes(request.getNotes() == null ? "" : request.getNotes().trim());
        return transactions.save(transaction);
    }

    @GetMapping("/farm-context")
    public Map<String, Object> farmContext(@RequestParam @NotBlank String district) {
        FarmerProfile profile = profiles.findById(1L)
                .filter(saved -> saved.getDistrict().equalsIgnoreCase(district.trim()))
                .orElse(null);
        List<CropPlan> cropPlans = profile == null ? List.of() : crops.findAllByOrderBySowingDateDesc();
        List<FarmTask> openTasks = profile == null ? List.of() : alerts();
        return Map.of(
                "profile", profile == null ? Map.of() : profile,
                "crops", cropPlans,
                "upcomingTasks", openTasks);
    }
}
