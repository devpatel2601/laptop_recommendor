package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.service.WordCompletionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/word-completion")
public class WordCompletionController {

    @Autowired
    private WordCompletionService wordCompletionService;

    @PostMapping("/build")
    public String buildVocabulary() {
        // Specify the file path
        String filePath = "product_names_vocabulary.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Read file content and build vocabulary
            Map<String, Integer> vocabulary = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase(); // Treat the entire line as a single word
                if (!line.isBlank()) {
                    vocabulary.put(line, vocabulary.getOrDefault(line, 0) + 1);
                }
            }

            // Build vocabulary in the AVL tree
            wordCompletionService.buildVocabulary(vocabulary);
            return "Vocabulary built successfully from file: " + filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return "An error occurred while reading the file: " + e.getMessage();
        }
    }



    // API to get word completions
    @GetMapping("/suggestions")
    public List<String> getSuggestions(@RequestParam String prefix, @RequestParam int topK) {
        return wordCompletionService.getSuggestions(prefix, topK);
    }
}
