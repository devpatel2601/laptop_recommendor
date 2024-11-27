package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.service.SearchFrequencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchFrequencyController {

    @Autowired
    private SearchFrequencyService searchFrequencyService;

    /**
     * Endpoint to increment the search count for a term.
     *
     * @param text the text in which the search term should be tracked
     * @param searchTerm the term to track
     * @return a success message
     */
    @PostMapping("/track")
    public ResponseEntity<String> trackSearch(@RequestParam String text, @RequestParam String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Search term cannot be null or empty.");
        }
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Text cannot be null or empty.");
        }
        searchFrequencyService.incrementSearchCount(text, searchTerm);
        return ResponseEntity.ok("Search term tracked successfully.");
    }

    /**
     * Endpoint to retrieve the search count of a specific term.
     *
     * @param searchTerm the term to retrieve the count for
     * @return the count of the term
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getSearchCount(@RequestParam String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        int count = searchFrequencyService.getSearchCount(searchTerm);
        return ResponseEntity.ok(count);
    }

    /**
     * Endpoint to reset the search count for a term to zero.
     *
     * @param searchTerm the term to reset
     * @return a success message
     */
    @DeleteMapping("/reset")
    public ResponseEntity<String> resetSearchCount(@RequestParam String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Search term cannot be null or empty.");
        }
        searchFrequencyService.resetSearchCount(searchTerm); // Implement reset logic in the service
        return ResponseEntity.ok("Search term count reset successfully.");
    }

    /**
     * Endpoint to retrieve all tracked search terms and their counts.
     *
     * @return a map of search terms and their counts
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Integer>> getAllSearchCounts() {
        Map<String, Integer> allSearchCounts = searchFrequencyService.getAllSearchCounts();
        return ResponseEntity.ok(allSearchCounts);
    }
}
