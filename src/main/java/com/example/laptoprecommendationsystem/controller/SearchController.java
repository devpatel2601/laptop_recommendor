package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.model.Laptop;
import com.example.laptoprecommendationsystem.service.VocabularyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private VocabularyService vocabularyService;

    // Endpoint to handle user search and suggest closest match using vocabulary.txt
    @GetMapping("/searchLaptop")
    public List<Laptop> searchLaptop(@RequestParam String searchTerm) {
        // Define the path to vocabulary.txt
        String vocabularyFilePath = "src/main/resources/vocabulary.txt";

        // Adjust the max edit distance as needed
        int maxEditDistance = 3;

        // Fetch the closest matches as full Laptop objects
        return vocabularyService.searchAndSuggestClosestMatches(searchTerm);
    }

}
