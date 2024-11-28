package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.model.Laptop;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EditDistanceService {

    // Function to calculate the Edit Distance between two words
    public static int calculateEditDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();

        // Create a DP table to store the results of subproblems
        int[][] dp = new int[len1 + 1][len2 + 1];

        // Fill dp[][] in a bottom-up manner
        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {
                if (i == 0) {
                    dp[i][j] = j; // j insertions
                } else if (j == 0) {
                    dp[i][j] = i; // i deletions
                } else if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], // Remove
                            Math.min(dp[i][j - 1],    // Insert
                                    dp[i - 1][j - 1])); // Replace
                }
            }
        }

        return dp[len1][len2];
    }

    public List<Laptop> findMatches(List<Laptop> laptops, String searchTerm, int maxEditDistance) {
        List<MatchResult> matches = new ArrayList<>();

        int numSpaces = searchTerm.split("\\s+").length - 1; // Spaces define word count
        int nGramSize = numSpaces + 1; // n-grams size for vocabulary

        for (Laptop laptop : laptops) {
            if (laptop == null || laptop.getProductName() == null) continue;

            String name = laptop.getProductName().toLowerCase();
            System.out.println("Checking product: " + name);

            String[] words = name.split("\\s+");

            for (String word : words) {
                if (word == null || word.isEmpty()) continue;

                int currentEditDistance = calculateEditDistance(searchTerm.toLowerCase(), word.toLowerCase());
                if (currentEditDistance <= maxEditDistance) {
                    matches.add(new MatchResult(laptop, currentEditDistance, 0));
                    System.out.println("Word match found: " + word);
                    break;
                }
            }

            List<String> nGrams = generateNGrams(words, nGramSize);

            for (String nGram : nGrams) {
                int currentEditDistance = calculateEditDistance(searchTerm.toLowerCase(), nGram.toLowerCase());
                if (currentEditDistance <= maxEditDistance) {
                    matches.add(new MatchResult(laptop, currentEditDistance, 1));
                    System.out.println("N-gram match found: " + nGram);
                    break;
                }
            }
        }

        // Sort matches using QuickSort
        quickSort(matches, 0, matches.size() - 1);

        return matches.stream()
                .map(MatchResult::getLaptop)
                .collect(Collectors.toList());
    }

    // QuickSort implementation
    private void quickSort(List<MatchResult> list, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(list, low, high);
            quickSort(list, low, pivotIndex - 1);
            quickSort(list, pivotIndex + 1, high);
        }
    }

    private int partition(List<MatchResult> list, int low, int high) {
        MatchResult pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (list.get(j).getFullEditDistance() < pivot.getFullEditDistance() ||
                    (list.get(j).getFullEditDistance() == pivot.getFullEditDistance() &&
                            list.get(j).getFirstNGramDistance() < pivot.getFirstNGramDistance())) {
                i++;
                swap(list, i, j);
            }
        }

        swap(list, i + 1, high);
        return i + 1;
    }

    private void swap(List<MatchResult> list, int i, int j) {
        MatchResult temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

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
