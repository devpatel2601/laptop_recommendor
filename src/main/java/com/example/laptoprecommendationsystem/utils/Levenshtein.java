package com.example.laptoprecommendationsystem.utils;

public class Levenshtein {

    // Calculate the Levenshtein distance between two strings
    public static int calculate(String s1, String s2) {
        int lenS1 = s1.length();
        int lenS2 = s2.length();

        // Create a matrix to store distances
        int[][] dp = new int[lenS1 + 1][lenS2 + 1];

        // Initialize the matrix
        for (int i = 0; i <= lenS1; i++) {
            for (int j = 0; j <= lenS2; j++) {
                if (i == 0) {
                    dp[i][j] = j;  // If first string is empty, distance is j (insert all chars)
                } else if (j == 0) {
                    dp[i][j] = i;  // If second string is empty, distance is i (insert all chars)
                } else {
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),  // Insertion or deletion
                            dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1)  // Substitution
                    );
                }
            }
        }

        return dp[lenS1][lenS2];  // Return the Levenshtein distance
    }
}
