package com.example.laptoprecommendationsystem.util;

import java.util.HashMap;
import java.util.Map;

public class BoyerMoore {

    private final String pattern;
    private final Map<Character, Integer> badCharTable;

    public BoyerMoore(String pattern) {
        this.pattern = pattern;
        this.badCharTable = buildBadCharTable(pattern);
    }

    // Search the pattern in the text and return the frequency count
    public int search(String text) {
        int m = pattern.length();
        int n = text.length();
        int count = 0;
        int shift = 0;

        while (shift <= (n - m)) {
            int j = m - 1;

            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
                j--;
            }

            if (j < 0) {
                count++;
                shift += (shift + m < n) ? m - badCharTable.getOrDefault(text.charAt(shift + m), -1) : 1;
            } else {
                shift += Math.max(1, j - badCharTable.getOrDefault(text.charAt(shift + j), -1));
            }
        }

        return count;
    }

    // Build bad character table
    private Map<Character, Integer> buildBadCharTable(String pattern) {
        Map<Character, Integer> table = new HashMap<>();
        for (int i = 0; i < pattern.length(); i++) {
            table.put(pattern.charAt(i), i);
        }
        return table;
    }
}
