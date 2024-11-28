package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.model.Laptop;
import com.example.laptoprecommendationsystem.repository.LaptopRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VocabularyService {

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
    @Autowired
    private LaptopRepository laptopRepository;
    @Autowired
    private EditDistanceService editDistanceService;

    int determineMaxEditDistance(String searchTerm) {
        int length = searchTerm.length();
        if (length <= 2) return 3; // Higher threshold for very short terms
        if (length <= 4) return 2; // Moderate threshold
        return 1; // Strict threshold for longer terms
    }
    public List<Laptop> searchAndSuggestClosestMatches(String searchTerm) {
          // Set based on your logic

        // Fetch laptops from the database
        List<Laptop> laptops = laptopRepository.findByProductNameContainingIgnoreCase(searchTerm);

        // If no exact matches, use edit distance to suggest close matches
        if (laptops.isEmpty()) {
            laptops = editDistanceService.findMatches(laptopRepository.findAll(), searchTerm, 3 );
        }

        return laptops;
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

        // Combine and filter matching words from both vocabularies, removing frequencies
        return Stream.concat(productNameVocabulary.stream(), wordVocabulary.stream())
                .filter(word -> word.toLowerCase().contains(lowerCaseInput))
                .map(word -> word.split(":")[0].trim()) // Remove frequency after ':' if present
                .collect(Collectors.toSet());
    }


    // Method to create product name vocabulary (unique product names)
    public Set<String> createProductNameVocabulary(String filePath) {
        Set<String> productNames = new HashSet<>(); // Use a Set to store unique product names

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                String productName = columns[0].trim().toLowerCase(); // Assuming the product name is in the first column
                productNames.add(productName); // Add the product name to the set
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return productNames;
    }

    public Set<String> createWordVocabularyFromExcel(String filePath) {
        Set<String> wordVocabulary = new HashSet<>();  // Use Set to store unique words
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
                            wordVocabulary.add(word);  // Add each word to the Set (duplicates are ignored)
                        }
                    }
                }
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordVocabulary;
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
    public void saveVocabularyToFile(Set<String> vocabulary, String outputFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (String word : vocabulary) {
                writer.write(word); // Just write the word (product name)
                writer.newLine(); // New line after each word
            }
            System.out.println("Vocabulary saved to " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
