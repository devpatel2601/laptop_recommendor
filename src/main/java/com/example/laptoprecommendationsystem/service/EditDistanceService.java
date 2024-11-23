package com.example.laptoprecommendationsystem.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EditDistanceService {

    // Method to calculate the Edit Distance (Levenshtein Distance)
    public int calculateEditDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();

        // Create a matrix to store the distances
        int[][] dp = new int[len1 + 1][len2 + 1];

        // Initialize base cases
        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {
                if (i == 0) {
                    dp[i][j] = j; // If first string is empty, insert all characters from second string
                } else if (j == 0) {
                    dp[i][j] = i; // If second string is empty, remove all characters from first string
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + (word1.charAt(i - 1) == word2.charAt(j - 1) ? 0 : 1));
                }
            }
        }
        return dp[len1][len2];
    }

    // Method to find the closest match for the misspelled search term using edit distance
    public String findClosestWordMatch(List<String> dictionary, String misspelledWord) {
        String closestWord = null;
        int smallestEditDistance = Integer.MAX_VALUE;

        for (String wordFromDictionary : dictionary) {
            // Calculate the edit distance for the current dictionary word
            int currentEditDistance = calculateEditDistance(misspelledWord, wordFromDictionary);

            // Update the closest match if the current word has a smaller edit distance
            if (currentEditDistance < smallestEditDistance) {
                smallestEditDistance = currentEditDistance;
                closestWord = wordFromDictionary;
            }
        }

        // Output the closest word match
        return closestWord != null ? closestWord : "No matches found.";
    }
}
