package com.example.laptoprecommendationsystem.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class VocabularyBuilderService {

    private Trie trie = new Trie();
    private Set<String> productNameVocabulary = new HashSet<>();
    private Set<String> wordVocabulary = new HashSet<>();

    // Load vocab from pre-built vocabulary files
    public void loadVocabularyFromFile() {
        loadVocabularyFromFile("product_name_vocabulary.txt", productNameVocabulary);


        // Load words into the Trie for spell check suggestions
        for (String word : productNameVocabulary) {
            trie.insert(word);
        }
        for (String word : wordVocabulary) {
            trie.insert(word);
        }
    }

    // Helper method to read words from a file and add them to the set
    private void loadVocabularyFromFile(String filePath, Set<String> vocabularySet) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (!word.isEmpty()) {
                    vocabularySet.add(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get words containing the input word as a substring from vocabulary
    public Set<String> getWordsContaining(String inputWord) {
        String lowerCaseInput = inputWord.toLowerCase();

        Set<String> matchingWords = new HashSet<>();
        matchingWords.addAll(productNameVocabulary.stream()
                .filter(word -> word.contains(lowerCaseInput))
                .collect(Collectors.toSet()));
        matchingWords.addAll(wordVocabulary.stream()
                .filter(word -> word.contains(lowerCaseInput))
                .collect(Collectors.toSet()));

        return matchingWords;
    }

    // Method to create word frequency of product names as a single word
    public Map<String, Integer> createProductNameVocabulary(String filePath) {
        Map<String, Integer> productFrequency = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                String productName = columns[0].trim().toLowerCase();
                productFrequency.put(productName, productFrequency.getOrDefault(productName, 0) + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return productFrequency;
    }

    // Method to create a vocabulary from every word in the excel file
    public Map<String, Integer> createWordVocabularyFromExcel(String filePath) {
        Map<String, Integer> wordFrequency = new HashMap<>();
        Pattern pattern = Pattern.compile("\\b\\w+\\b");

        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                for (Cell cell : row) {
                    String cellValue = getCellValueAsString(cell);
                    if (cellValue != null) {
                        cellValue = cellValue.toLowerCase();
                        var matcher = pattern.matcher(cellValue);

                        while (matcher.find()) {
                            String word = matcher.group();
                            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                        }
                    }
                }
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordFrequency;
    }

    // Helper method to get the cell value as a String
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    // Method to save vocabulary to a file
    public void saveVocabularyToFile(Map<String, Integer> vocabulary, String outputFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (Map.Entry<String, Integer> entry : vocabulary.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
            System.out.println("Vocabulary saved to " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Endpoint for generating and retrieving the word vocabulary
    @GetMapping("/buildWordVocabulary")
    public Map<String, Integer> getWordVocabulary() {
        String inputFilePath = "src/main/resources/products-Excel.xlsx";
        Map<String, Integer> vocabulary = createWordVocabularyFromExcel(inputFilePath);

        String outputFilePath = "word_vocabulary.txt";
        saveVocabularyToFile(vocabulary, outputFilePath);

        return vocabulary;
    }
}
