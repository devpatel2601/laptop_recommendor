package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.model.Laptop;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EditDistanceService {

    // Method to calculate the Edit Distance (Levenshtein Distance)
    // Function to calculate the Edit Distance between two words
    public static int calculateEditDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();

        // Create a DP table to store the results of subproblems
        int[][] dp = new int[len1 + 1][len2 + 1];

        // Fill dp[][] in a bottom-up manner
        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {
                // If the first word is empty, insert all characters of the second word
                if (i == 0) {
                    dp[i][j] = j; // j insertions
                }
                // If the second word is empty, remove all characters of the first word
                else if (j == 0) {
                    dp[i][j] = i; // i deletions
                }
                // If the last characters of both words are the same, ignore the last character
                // and recur for the remaining words
                else if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
                // If the last characters are different, consider all possibilities:
                // insert, delete, or replace the last character
                else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], // Remove
                            Math.min(dp[i][j - 1],    // Insert
                                    dp[i - 1][j - 1])); // Replace
                }
            }
        }

        // The final value in dp[len1][len2] will be the answer
        return dp[len1][len2];
    }

    public List<Laptop> findMatches(List<Laptop> laptops, String searchTerm, int maxEditDistance) {
//        if (searchTerm == null || searchTerm.isEmpty()) {
//            return List.of(new Laptop("Invalid input: search term cannot be null or empty."));
//        }
//        if (laptops == null || laptops.isEmpty()) {
//            return List.of(new Laptop("Dictionary is empty."));
//        }

        List<MatchResult> matches = new ArrayList<>();

        // Determine the number of words in the search term (n)
        int numSpaces = searchTerm.split("\\s+").length - 1; // Spaces define word count
        int nGramSize = numSpaces + 1; // n-grams size for vocabulary

        for (Laptop laptop : laptops) {
            if (laptop == null || laptop.getProductName() == null) continue;

            String name = laptop.getProductName().toLowerCase();
            System.out.println("Checking product: " + name);  // Debug: show the product being checked

            // Tokenize the entry into words
            String[] words = name.split("\\s+");

            // Check individual words for edit distance
            for (String word : words) {
                if (word == null || word.isEmpty()) continue;

                int currentEditDistance = calculateEditDistance(searchTerm.toLowerCase(), word.toLowerCase());
                if (currentEditDistance <= maxEditDistance) {
                    matches.add(new MatchResult(laptop, currentEditDistance, 0));
                    System.out.println("Word match found: " + word);  // Debug: show word match
                    break; // No need to check further words for this entry
                }
            }

            // Generate n-grams (sequences of nGramSize words)
            List<String> nGrams = generateNGrams(words, nGramSize);

            for (String nGram : nGrams) {
                // Case-insensitive comparison for n-grams
                int currentEditDistance = calculateEditDistance(searchTerm.toLowerCase(), nGram.toLowerCase());
                if (currentEditDistance <= maxEditDistance) {
                    matches.add(new MatchResult(laptop, currentEditDistance, 1)); // n-gram match
                    System.out.println("N-gram match found: " + nGram);  // Debug: show n-gram match
                    break; // No need to check further n-grams for this entry
                }
            }
        }

        // Sort matches: first by full edit distance, then by n-gram distance
        matches.sort(Comparator
                .comparingInt(MatchResult::getFullEditDistance)
                .thenComparingInt(MatchResult::getFirstNGramDistance));

        // Return the sorted list of laptops
        return matches.stream()
                .map(MatchResult::getLaptop)
                .collect(Collectors.toList());
    }

    // Helper method to generate overlapping n-grams
    private List<String> generateNGrams(String[] words, int nGramSize) {
        List<String> nGrams = new ArrayList<>();
        for (int i = 0; i <= words.length - nGramSize; i++) {
            StringBuilder nGram = new StringBuilder();
            for (int j = 0; j < nGramSize; j++) {
                if (j > 0) nGram.append(" ");
                nGram.append(words[i + j]);
            }
            nGrams.add(nGram.toString());
        }
        return nGrams;
    }

    // MatchResult class for storing laptop and distances
    private static class MatchResult {
        private final Laptop laptop;
        private final int fullEditDistance;
        private final int firstNGramDistance;

        public MatchResult(Laptop laptop, int fullEditDistance, int firstNGramDistance) {
            this.laptop = laptop;
            this.fullEditDistance = fullEditDistance;
            this.firstNGramDistance = firstNGramDistance;
        }

        public Laptop getLaptop() {
            return laptop;
        }

        public int getFullEditDistance() {
            return fullEditDistance;
        }

        public int getFirstNGramDistance() {
            return firstNGramDistance;
        }
    }
}