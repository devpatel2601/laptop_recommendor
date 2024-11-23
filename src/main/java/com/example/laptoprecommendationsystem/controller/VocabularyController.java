package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.service.VocabularyBuilderService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/vocabulary")
public class VocabularyController {

    @Autowired
    private VocabularyBuilderService vocabularyBuilderService;

    // This method is called when the Spring Boot application starts
    @PostConstruct
    public void init() {
        vocabularyBuilderService.loadVocabularyFromFile();
    }

    // Endpoint for getting words containing the input word as a substring
    @GetMapping("/spellcheck")
    public Set<String> getWordsContaining(@RequestParam String word) {
        return vocabularyBuilderService.getWordsContaining(word);
    }

    // Endpoint for generating and retrieving the product name vocabulary
    @GetMapping("/buildProductNameVocabulary")
    public Map<String, Integer> getProductNameVocabulary() {
        String inputFilePath = "src/main/resources/products.csv";
        Map<String, Integer> vocabulary = vocabularyBuilderService.createProductNameVocabulary(inputFilePath);

        // Optionally, save the vocabulary to a file
        String outputFilePath = "product_name_vocabulary.txt";
        vocabularyBuilderService.saveVocabularyToFile(vocabulary, outputFilePath);

        return vocabulary;
    }

    // Endpoint for generating and retrieving all word vocabulary
    @GetMapping("/buildWordVocabulary")
    public Map<String, Integer> getWordVocabulary() {
        String inputFilePath = "src/main/resources/products-Excel.xlsx";
        Map<String, Integer> vocabulary = vocabularyBuilderService.createWordVocabularyFromExcel(inputFilePath);

        // Optionally, save the vocabulary to a file
        String outputFilePath = "word_vocabulary.txt";
        vocabularyBuilderService.saveVocabularyToFile(vocabulary, outputFilePath);

        return vocabulary;
    }
}
