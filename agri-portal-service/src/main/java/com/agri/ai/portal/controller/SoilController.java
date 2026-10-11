package com.agri.ai.portal.controller;

import com.agri.ai.portal.dto.SoilResultRequest;
import com.agri.ai.portal.model.SoilResult;
import com.agri.ai.portal.repository.SoilResultRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Validated
@RestController
@RequestMapping("/api/portal/soil")
public class SoilController {
    private static final Map<String, String> UNITS = Map.of(
            "pH", "pH", "EC", "dS/m", "Nitrate", "mg/L", "Phosphate", "mg/L",
            "Lab nitrogen", "kg/ha", "Lab phosphorus", "kg/ha", "Lab potassium", "kg/ha",
            "Texture", "%", "Drainage", "mm/hour", "Organic matter", "%");
    private static final Set<String> METHODS = Set.of("Lab", "Commercial kit", "Home screening");
    private final SoilResultRepository results;

    public SoilController(SoilResultRepository results) {
        this.results = results;
    }

    @GetMapping("/tests")
    public List<Map<String, Object>> catalogue() {
        return List.of(
                test("Jar sedimentation", "Texture estimate", "Jar, clean water, small amount of dish soap, ruler",
                        "Mix one part soil with water and a small amount of soap, shake well, then let it settle. Measure layers only after the clay layer has settled; this can take 48–72 hours.", "2–3 days", "★★★ screening", "Home screening", "Do not drink the mixture; wash hands and keep it away from children."),
                test("Ribbon / feel", "Approximate clay content", "Moist soil and clean hands",
                        "Moisten a small sample and gently press it between thumb and finger to see whether it forms a ribbon.", "5 minutes", "★★ screening", "Home screening", "Avoid soil from contaminated sites; wash hands."),
                test("pH strips", "Approximate soil acidity / alkalinity", "Commercial pH strips and distilled water",
                        "Follow kit instructions. A common screening approach uses a measured soil-to-water extract; use the same method each time.", "10 minutes", "★★★ screening kit", "Commercial kit", "Use only commercial kits as labelled; colour readings are approximate."),
                test("pH + EC pen", "Approximate pH and salinity indicator", "Calibrated pH/EC meter and distilled water",
                        "Calibrate the instrument and follow its specified soil-extract ratio and temperature guidance.", "10 minutes", "★★★ screening kit", "Commercial kit", "EC interpretation depends on the extraction method; record that method."),
                test("Vinegar fizz", "Possible carbonate presence only", "Household vinegar",
                        "Add a few drops to dry soil. A fizz may suggest carbonates; no fizz does not rule them out.", "5 minutes", "★ very limited", "Home screening", "Do not treat this as a pH measurement or soil diagnosis."),
                test("Percolation", "Field drainage screening", "Shovel, water, ruler and timer",
                        "Dig a small test hole, pre-wet it, refill to a measured depth and time the second drain. Compare only similar soil moisture and hole dimensions.", "1–4 hours", "★★★ field screening", "Home screening", "Check for buried services and obtain permission before digging."),
                test("Slake / aggregate", "Visual aggregate stability proxy", "Clear jar and clean water",
                        "Place a dry soil aggregate in water and observe whether it stays intact or disperses. This is a visual comparison, not a lab organic-matter test.", "5 minutes", "★★ visual", "Home screening", "Avoid drawing nutrient conclusions from this observation."),
                test("Lab soil health card", "Lab-measured pH, EC, organic carbon and nutrients including N, P and K", "Soil Health Card or accredited laboratory report",
                        "Follow the lab's sampling instructions and enter each result with its unit and test date.", "As reported by lab", "Lab report", "Lab", "Laboratory guidance is the reference for fertilizer decisions."));
    }

    @GetMapping("/shopping-list")
    public List<Map<String, String>> shoppingList(@RequestParam List<String> tests) {
        return tests.stream().distinct().map(name -> switch (name) {
            case "pH strips" -> Map.of("test", name, "items", "Commercial soil pH strips, distilled water, clean sample cup", "priceNote", "Prices vary by brand and location; check the seller before purchase.");
            case "pH + EC pen" -> Map.of("test", name, "items", "Calibratable pH/EC meter, pH buffer solutions, distilled water", "priceNote", "Choose a meter with calibration instructions; no fixed local price is claimed.");
            case "Jar sedimentation" -> Map.of("test", name, "items", "Clear jar with lid, ruler, clean water, small amount of dish soap", "priceNote", "Household materials; do not use contaminated containers.");
            case "Percolation" -> Map.of("test", name, "items", "Shovel, timer, ruler or measuring tape", "priceNote", "Borrow tools where possible; check for buried utilities.");
            case "Lab soil health card" -> Map.of("test", name, "items", "Clean sample bags and the laboratory's sampling instructions", "priceNote", "Ask the lab or local agriculture office about current fees and sample collection.");
            default -> Map.of("test", name, "items", "Check the catalogue and use only the listed safe materials.", "priceNote", "No price estimate available.");
        }).toList();
    }

    @GetMapping("/shops")
    public List<Map<String, String>> labAndShopDirectories() {
        return List.of(
                Map.of("name", "Soil Health Card programme", "type", "Official government information", "url", "https://soilhealth.dac.gov.in/"),
                Map.of("name", "ICAR Krishi Vigyan Kendra directory", "type", "Extension and local testing guidance", "url", "https://kvk.icar.gov.in/"),
                Map.of("name", "Tamil Nadu Agricultural University", "type", "State agricultural university information", "url", "https://tnau.ac.in/"));
    }

    @GetMapping("/results")
    public List<SoilResult> history(@RequestParam(required = false) String testType) {
        if (testType == null || testType.isBlank()) return results.findAllByOrderByTestedOnDescCreatedAtDesc();
        if (!UNITS.containsKey(testType)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown soil test type.");
        return results.findAllByTestTypeOrderByTestedOnAsc(testType);
    }

    @GetMapping("/trends")
    public Map<String, List<SoilResult>> trends() {
        return UNITS.keySet().stream().collect(java.util.stream.Collectors.toMap(
                name -> name,
                results::findAllByTestTypeOrderByTestedOnAsc,
                (left, right) -> left,
                java.util.LinkedHashMap::new));
    }

    @PostMapping("/results")
    @ResponseStatus(HttpStatus.CREATED)
    public SoilResult save(@Valid @RequestBody SoilResultRequest request) {
        String testType = request.getTestType().trim();
        String method = request.getMethod().trim();
        if (!UNITS.containsKey(testType)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a listed soil test.");
        if (!METHODS.contains(method)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a listed measurement method.");
        if (testType.startsWith("Lab ") && !"Lab".equals(method)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "N, P and K values must come from a laboratory report.");
        }
        if (!isSupportedUnit(testType, method, request.getUnit())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use the unit required for this soil test.");
        }
        if (request.getTestedOn().isAfter(LocalDate.now())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Test date cannot be in the future.");
        validateRange(testType, request.getValue());
        SoilResult result = new SoilResult();
        result.setTestType(testType);
        result.setCropName(request.getCropName() == null ? "" : request.getCropName().trim());
        result.setFieldName(request.getFieldName().trim());
        result.setValue(request.getValue());
        result.setUnit(request.getUnit());
        result.setMethod(method);
        result.setAccuracyClass(accuracyClass(method));
        result.setTestedOn(request.getTestedOn());
        result.setNotes(request.getNotes() == null ? "" : request.getNotes().trim());
        result.setInterpretation(interpret(testType, request.getValue(), method));
        result.setCreatedAt(Instant.now());
        return results.save(result);
    }

    private boolean isSupportedUnit(String testType, String method, String unit) {
        if (UNITS.get(testType).equals(unit)) return true;
        if (!"Lab".equals(method)) return false;
        return switch (testType) {
            case "Lab nitrogen", "Lab phosphorus", "Lab potassium" -> Set.of("mg/kg", "ppm").contains(unit);
            case "Organic matter" -> Set.of("g/kg").contains(unit);
            default -> false;
        };
    }

    @DeleteMapping("/results/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        if (!results.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Soil result not found.");
        results.deleteById(id);
    }

    private Map<String, Object> test(String name, String measures, String materials, String steps, String time,
                                     String accuracy, String method, String safety) {
        return Map.of("name", name, "measures", measures, "materials", materials, "steps", steps,
                "time", time, "accuracy", accuracy, "method", method, "safety", safety);
    }

    private String accuracyClass(String method) {
        return switch (method) {
            case "Lab" -> "Laboratory result";
            case "Commercial kit" -> "Kit screening";
            default -> "Home screening";
        };
    }

    private void validateRange(String testType, BigDecimal value) {
        boolean invalid = switch (testType) {
            case "pH" -> value.compareTo(BigDecimal.ZERO) <= 0 || value.compareTo(BigDecimal.valueOf(14)) > 0;
            case "EC" -> value.compareTo(BigDecimal.valueOf(100)) > 0;
            case "Texture" -> value.compareTo(BigDecimal.valueOf(100)) > 0;
            case "Drainage" -> value.compareTo(BigDecimal.valueOf(10000)) > 0;
            case "Organic matter", "Nitrate", "Phosphate", "Lab nitrogen", "Lab phosphorus", "Lab potassium" ->
                    value.compareTo(BigDecimal.valueOf(1000000)) > 0;
            default -> true;
        };
        if (invalid) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reading is outside the supported range for this test.");
    }

    private String interpret(String testType, BigDecimal value, String method) {
        String confidence = "Lab".equals(method) ? "" : " Screening only; confirm with a soil laboratory before major input decisions.";
        return switch (testType) {
            case "pH" -> value.compareTo(BigDecimal.valueOf(5.5)) < 0
                    ? "Acidic-range reading." + confidence
                    : value.compareTo(BigDecimal.valueOf(7.5)) > 0
                    ? "Alkaline-range reading." + confidence
                    : "Near-neutral-range reading." + confidence;
            case "EC" -> value.compareTo(BigDecimal.valueOf(2)) >= 0
                    ? "Elevated conductivity screening value; confirm method and salinity with a laboratory before action."
                    : "Below the app's simple 2 dS/m screening flag; this does not rule out salinity or sodicity.";
            case "Texture" -> "Saved as a texture observation. Texture percentages should be measured and recorded consistently.";
            case "Drainage" -> value.compareTo(BigDecimal.valueOf(5)) < 0
                    ? "Slow infiltration screening result; assess the field and confirm locally before changing drainage."
                    : "Saved as a drainage screening result; interpretation depends on soil moisture and test setup.";
            default -> "Reading saved. Interpretation depends on the test method and lab-specific reference range; no fertilizer dose is generated.";
        };
    }
}
