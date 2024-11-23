package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.service.VocabularyService;
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
    private VocabularyService vocabularyBuilderService;

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

    @GetMapping("/buildProductNameVocabulary")
    public Set<String> getProductNameVocabulary() {
        String inputFilePath = "src/main/resources/products.csv"; // Your file path
        Set<String> productNames = vocabularyBuilderService.createProductNameVocabulary(inputFilePath);

        // Optionally, save the vocabulary to a file (if required)
        String outputFilePath = "product_names_vocabulary.txt";
        vocabularyBuilderService.saveVocabularyToFile(productNames, outputFilePath);

        return productNames; // Return the Set of product names
    }

    @GetMapping("/buildWordVocabulary")
    public Set<String> getWordVocabulary() {
        String inputFilePath = "src/main/resources/products-Excel.xlsx";
        Set<String> vocabulary = vocabularyBuilderService.createWordVocabularyFromExcel(inputFilePath);

        // Optionally, save the vocabulary to a file
        String outputFilePath = "word_vocabulary.txt";
        vocabularyBuilderService.saveVocabularyToFile(vocabulary, outputFilePath);  // Pass Set to the save method

        return vocabulary;
    }

}
