package com.hackathon.agri.portal.controller;

import com.hackathon.agri.portal.dto.AdvisoryResponse;
import com.hackathon.agri.portal.service.PortalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

@Controller
public class PortalController {

    private final PortalService portalService;

    public PortalController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("question", "");
        model.addAttribute("district", "");
        model.addAttribute("preferredLanguage", "English");
        model.addAttribute("history", portalService.getRecentQueries());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("analytics", portalService.getAnalytics());
        model.addAttribute("history", portalService.getRecentQueries());
        return "dashboard";
    }

    @PostMapping("/ask")
    public String ask(
            @RequestParam String question,
            @RequestParam String district,
            @RequestParam(defaultValue = "English") String preferredLanguage,
            Model model) {
        String cleanQuestion = question.trim();
        String cleanDistrict = district.trim();
        String cleanLanguage = preferredLanguage.trim();
        model.addAttribute("question", cleanQuestion);
        model.addAttribute("district", cleanDistrict);
        model.addAttribute("preferredLanguage", cleanLanguage);

        if (cleanQuestion.isBlank() || cleanDistrict.isBlank()) {
            model.addAttribute("error", "Please enter both your question and district.");
        } else {
            try {
                AdvisoryResponse response = portalService.getAdvice(cleanQuestion, cleanDistrict, cleanLanguage);
                model.addAttribute("response", response);
            } catch (RestClientException | IllegalStateException exception) {
                model.addAttribute("error",
                        "We couldn't generate advice. Check that the advisory service is running and Ollama is ready with the configured model.");
            }
        }

        model.addAttribute("history", portalService.getRecentQueries());
        return "index";
    }
}
