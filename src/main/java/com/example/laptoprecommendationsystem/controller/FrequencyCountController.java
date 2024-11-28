package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.service.FrequencyCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling frequency count requests.
 */
@RestController
public class FrequencyCountController {

    @Autowired
    private FrequencyCountService frequencyCountService;

    /**
     * Endpoint to get the total frequency count of the search term in all laptops on the page.
     * @param searchTerm The term to search for in laptops.
     * @param page The page number for pagination.
     * @param size The number of laptops per page.
     * @return The total frequency count of the search term across all laptops on the page.
     */
    @GetMapping("/frequency-count")
    public int getFrequencyCount(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){
        // Call the service to get the total frequency count
        return frequencyCountService.getTotalFrequencyCount(searchTerm, page, size);
    }
}
