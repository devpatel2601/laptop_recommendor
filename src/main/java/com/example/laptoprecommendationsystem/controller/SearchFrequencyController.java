package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.service.SearchFrequencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchFrequencyController {

    @Autowired
    private SearchFrequencyService searchFrequencyService;

    // Endpoint to increment the search count for a term
    @PostMapping("/track")
    public void trackSearch(@RequestParam String searchTerm) {
        searchFrequencyService.incrementSearchCount(searchTerm);
    }

    // Endpoint to retrieve the search count of a specific term
    @GetMapping("/count")
    public int getSearchCount(@RequestParam String searchTerm) {
        return searchFrequencyService.getSearchCount(searchTerm);
    }
}
