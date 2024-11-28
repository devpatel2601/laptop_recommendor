package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.model.Laptop;
import com.example.laptoprecommendationsystem.service.VocabularyService;
import com.example.laptoprecommendationsystem.service.InvertedIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private VocabularyService vocabularyService;

    @Autowired
    private InvertedIndexService invertedIndexService;

    private static final String FILE_PATH = "src/main/resources/Products_Final.xlsx"; // Path to the file containing laptop data

    // Endpoint to handle user search and suggest closest match using vocabulary.txt (Edit Distance Only)
    @GetMapping("/searchLaptop")
    public List<Laptop> searchLaptop(@RequestParam String searchTerm) {
        // Define the path to vocabulary.txt
        String vocabularyFilePath = "product_names_vocabulary.txt";

        // Adjust the max edit distance as needed
        int maxEditDistance = 3;
        
        // Fetch the closest matches as full Laptop objects
        return vocabularyService.searchAndSuggestClosestMatches(searchTerm);
    }

    @GetMapping("/fullSearch")
    public List<List<Laptop>> fullSearch(@RequestParam String searchTerm) {
        // Step 1: Fetch laptops from vocabulary service or other data sources
        List<Laptop> laptops = vocabularyService.searchAndSuggestClosestMatches(searchTerm);

        // Step 2: Build the inverted index using the fetched laptops
        buildInvertedIndex(laptops);

        // Step 3: Search for matching indexes using the Inverted Index
        List<Integer> matchingPageIndexes = invertedIndexService.searchByPrefix(searchTerm.toLowerCase());

        // Step 4: Filter and validate laptops based on matching indexes
        List<Laptop> matchingLaptops = filterLaptopsByIndexes(laptops, matchingPageIndexes);

        // Return the result as a list of lists of laptops
        List<List<Laptop>> result = new ArrayList<>();
        result.add(matchingLaptops);  // You can add more lists if necessary
        return result;
    }


    // Endpoint for Inverted Indexing Search (Steps 1-3)
    @GetMapping("/searchByInvertedIndex")
    public List<Laptop> searchByInvertedIndex(@RequestParam String searchTerm) {
        List<Laptop> laptops = vocabularyService.searchAndSuggestClosestMatches(searchTerm);
        buildInvertedIndex(laptops);
        List<Integer> matchingPageIndexes = invertedIndexService.searchByPrefix(searchTerm.toLowerCase());
        return filterLaptopsByIndexes(laptops, matchingPageIndexes);
    }

    /**
     * Builds the inverted index by inserting words from each laptop into the Trie.
     */
    private void buildInvertedIndex(List<Laptop> laptops) {
        for (int i = 0; i < laptops.size(); i++) {
            Laptop laptop = laptops.get(i);
            String text = (laptop.getProductName() + " " + laptop.getBrandName()).toLowerCase();
            String[] words = text.split("\\W+");
            for (String word : words) {
                invertedIndexService.insert(word, i); // Insert words with their index
            }
        }
    }

    /**
     * Filters laptops based on the matching indexes from the inverted index.
     */
    private List<Laptop> filterLaptopsByIndexes(List<Laptop> laptops, List<Integer> matchingIndexes) {
        return matchingIndexes.stream()
                .filter(index -> index >= 0 && index < laptops.size()) // Ensure indexes are within bounds
                .map(laptops::get)
                .toList();
    }

    /**
     * Placeholder method to fetch laptops from a data source.
     * Replace with actual logic to fetch laptops from a database or API.
     */
    private List<Laptop> fetchLaptops() {
        // Replace this with actual implementation
        return new ArrayList<>();
    }
}
