package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.model.SearchFrequency;
import com.example.laptoprecommendationsystem.repository.SearchFrequencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Service
public class SearchFrequencyService {

    @Autowired
    private SearchFrequencyRepository searchFrequencyRepository;

    /**
     * Increments the search count for the given search term.
     * The KMP algorithm is used to check if the term exists within the text.
     *
     * @param searchTerm the term whose frequency is to be tracked
     * @param term
     */
    @Transactional
    public void incrementSearchCount(String searchTerm, String term) {
        // Normalize the search term to ensure consistency
        String normalizedSearchTerm = normalizeSearchTerm(searchTerm);

        // Log for debugging
        System.out.println("Normalized search term: " + normalizedSearchTerm);

        // Use the KMP algorithm to check if the search term exists in the provided text (term)
        boolean isTermFound = searchUsingKMP(term, normalizedSearchTerm);

        if (isTermFound) {
            // Check if the search term already exists in the database
            Optional<SearchFrequency> existingSearch = searchFrequencyRepository.findBySearchTerm(normalizedSearchTerm);

            if (existingSearch.isPresent()) {
                // If the search term exists, increment its count
                SearchFrequency searchFrequency = existingSearch.get();
                System.out.println("Found existing search term. Current count: " + searchFrequency.getSearchCount());

                int newCount = searchFrequency.getSearchCount() + 1;
                searchFrequency.setSearchCount(newCount);

                // Save the updated count (flush removed here)
                searchFrequencyRepository.save(searchFrequency);

                // Log updated count for debugging
                System.out.println("Incremented count: " + newCount);
            } else {
                // If the search term does not exist, create a new record with count 1
                SearchFrequency newSearchFrequency = new SearchFrequency();
                newSearchFrequency.setSearchTerm(normalizedSearchTerm);
                newSearchFrequency.setSearchCount(1);

                searchFrequencyRepository.save(newSearchFrequency);

                // Log for debugging
                System.out.println("New search term added with count 1: " + normalizedSearchTerm);
            }
        } else {
            System.out.println("Search term not found in the provided text.");
        }
    }


    /**
     * Retrieves the search count for a specific search term.
     *
     * @param searchTerm the term whose frequency is to be retrieved
     * @return the search count for the term, or 0 if not found
     */
    public int getSearchCount(String searchTerm) {
        String normalizedSearchTerm = normalizeSearchTerm(searchTerm);

        // Fetch the count from the repository, defaulting to 0 if not found
        return searchFrequencyRepository
                .findBySearchTerm(normalizedSearchTerm)
                .map(SearchFrequency::getSearchCount)
                .orElse(0);
    }

    /**
     * Normalizes a search term by converting it to lowercase and trimming whitespace.
     *
     * @param searchTerm the term to normalize
     * @return the normalized search term
     */
    private String normalizeSearchTerm(String searchTerm) {
        if (searchTerm == null) {
            throw new IllegalArgumentException("Search term cannot be null");
        }
        return searchTerm.trim().toLowerCase();
    }

    /**
     * KMP algorithm for searching a pattern in a text.
     * This method returns true if the search term (pattern) exists in the provided text.
     *
     * @param text the text in which to search for the pattern
     * @param pattern the search term (pattern)
     * @return true if the pattern is found in the text, false otherwise
     */
    private boolean searchUsingKMP(String text, String pattern) {
        int[] lps = createLPSArray(pattern); // Generate the longest prefix suffix (LPS) array
        int i = 0; // Text index
        int j = 0; // Pattern index

        while (i < text.length()) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            if (j == pattern.length()) {
                return true; // Pattern found, return true
            } else if (i < text.length() && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1]; // Use LPS array to skip comparisons
                } else {
                    i++;
                }
            }
        }
        return false; // Pattern not found, return false
    }

    /**
     * Creates the LPS (Longest Prefix Suffix) array for the pattern.
     * This helps in skipping unnecessary comparisons in the KMP algorithm.
     *
     * @param pattern the search term (pattern)
     * @return the LPS array
     */
    private int[] createLPSArray(String pattern) {
        int length = 0; // Length of the previous longest prefix suffix
        int i = 1; // Starts from the second character
        int[] lps = new int[pattern.length()];
        lps[0] = 0; // LPS for the first character is always 0

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length; // Set the LPS value
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1]; // Reset length to the previous LPS value
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Retrieves all search terms and their associated counts.
     *
     * @return a map of search terms and their counts
     */
    public Map<String, Integer> getAllSearchCounts() {
        Map<String, Integer> searchCounts = new HashMap<>();
        Iterable<SearchFrequency> searchFrequencies = searchFrequencyRepository.findAll();

        for (SearchFrequency searchFrequency : searchFrequencies) {
            searchCounts.put(searchFrequency.getSearchTerm(), searchFrequency.getSearchCount());
        }
        return searchCounts;
    }

    /**
     * Resets the search count for a specific search term to zero.
     *
     * @param searchTerm the term whose count will be reset
     */
    @Transactional
    public void resetSearchCount(String searchTerm) {
        String normalizedSearchTerm = normalizeSearchTerm(searchTerm);
        Optional<SearchFrequency> existingSearch = searchFrequencyRepository.findBySearchTerm(normalizedSearchTerm);

        if (existingSearch.isPresent()) {
            SearchFrequency searchFrequency = existingSearch.get();
            searchFrequency.setSearchCount(0); // Reset count to 0
            searchFrequencyRepository.save(searchFrequency);
        }
    }
}
