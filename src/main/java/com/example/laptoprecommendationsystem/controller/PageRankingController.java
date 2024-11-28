package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.service.PageRankingService;
import com.example.laptoprecommendationsystem.service.PageRankingService.PageRank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for handling requests to rank pages based on search term frequency.
 */
@RestController
public class PageRankingController {

    @Autowired
    private PageRankingService pageRankingService;

    /**
     * Endpoint to get ranked pages based on search term frequency.
     * @param searchTerm The term to search for in laptop data.
     * @param pageSize The number of laptops per page.
     * @return A list of ranked pages with their total frequency counts.
     */
    @GetMapping("/rank-pages")
    public List<PageRank> rankPages(
            @RequestParam("searchTerm") String searchTerm,
            @RequestParam(defaultValue = "20") int pageSize) {
        // Call the service to get the ranked pages
        return pageRankingService.getRankedPages(searchTerm, pageSize);
    }
}
