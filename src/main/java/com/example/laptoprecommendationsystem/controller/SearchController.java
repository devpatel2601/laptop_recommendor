package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.service.VocabularyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    @Autowired
    private VocabularyService vocabularyService;

    // Endpoint to handle user search and suggest closest match using vocabulary.txt
    @GetMapping("/searchLaptop")
    public String searchLaptop(@RequestParam String searchTerm) {
        // Define the path to vocabulary.txt
        String vocabularyFilePath = "src/main/resources/vocabulary.txt";

        // Suggest the closest match from available laptops listed in vocabulary.txt
        return vocabularyService.searchAndSuggestClosestMatch(searchTerm, vocabularyFilePath);
    }
}
