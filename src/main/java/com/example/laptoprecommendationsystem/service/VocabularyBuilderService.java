package com.example.laptoprecommendationsystem.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class VocabularyBuilderService {

    // Method to create word frequency of product names as a single word
    public Map<String, Integer> createProductNameVocabulary(String filePath) {
        Map<String, Integer> productFrequency = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                // Split the line by commas (assuming CSV format) and get the "Product Name" column
                String[] columns = line.split(",");
                String productName = columns[0].trim();  // Assuming the product name is the first column

                // Convert product name to lowercase and count its frequency
                productName = productName.toLowerCase();

                // Update frequency count in the map
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
        Pattern pattern = Pattern.compile("\\b\\w+\\b"); // Regex to match words

        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = new XSSFWorkbook(fis); // Using XSSFWorkbook for .xlsx files
            Sheet sheet = workbook.getSheetAt(0);  // Read first sheet

            // Iterate over each row
            for (Row row : sheet) {
                for (Cell cell : row) {
                    String cellValue = getCellValueAsString(cell);
                    if (cellValue != null) {
                        // Convert cell value to lowercase and find all words
                        cellValue = cellValue.toLowerCase();
                        var matcher = pattern.matcher(cellValue);

                        // Count each word's frequency
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
}
