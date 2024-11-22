package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.service.VocabularyBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/vocabulary")
public class VocabularyController {

    @Autowired
    private VocabularyBuilderService vocabularyBuilderService;

    // Endpoint for product name vocabulary
    @GetMapping("/buildProductNameVocabulary")
    public Map<String, Integer> getProductNameVocabulary() {
        String inputFilePath = "src/main/resources/products.csv";
        // Step 1: Generate product name vocabulary
        Map<String, Integer> vocabulary = vocabularyBuilderService.createProductNameVocabulary(inputFilePath);

        // Optionally, save the vocabulary to a file
        String outputFilePath = "product_name_vocabulary.txt";
        vocabularyBuilderService.saveVocabularyToFile(vocabulary, outputFilePath);

        // Return the vocabulary
        return vocabulary;
    }

    // Endpoint for all word vocabulary
    @GetMapping("/buildWordVocabulary")
    public Map<String, Integer> getWordVocabulary() {
        String inputFilePath = "src/main/resources/products-Excel.xlsx";
        // Step 2: Generate word vocabulary from entire Excel
        Map<String, Integer> vocabulary = vocabularyBuilderService.createWordVocabularyFromExcel(inputFilePath);

        // Optionally, save the vocabulary to a file
        String outputFilePath = "word_vocabulary.txt";
        vocabularyBuilderService.saveVocabularyToFile(vocabulary, outputFilePath);

        // Return the vocabulary
        return vocabulary;
    }
}
