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
import java.util.Set;

@RestController
public class SearchController {

    @Autowired
    private VocabularyService vocabularyService;

    @Autowired
    private InvertedIndexService invertedIndexService;

    /**
     * Search for laptops using edit distance logic to find the closest matches.
     *
     * @param searchTerm The term to search for.
     * @return A list of laptops matching the closest edit distance.
     */
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

    /**
     * Perform a full search, combining edit distance suggestions and inverted index search.
     *
     * @param searchTerm The term to search for.
     * @return A list of lists of laptops matching the search criteria.
     */
    @GetMapping("/fullSearch")
    public List<List<Laptop>> fullSearch(@RequestParam String searchTerm) {
        // Step 1: Fetch laptops using edit distance logic
        List<Laptop> laptops = vocabularyService.searchAndSuggestClosestMatches(searchTerm);

        // Step 2: Build the inverted index with fetched laptops
        buildInvertedIndex(laptops);

        // Step 3: Search using inverted index
        Set<Long> matchingLaptopIds = invertedIndexService.searchByPrefix(searchTerm.toLowerCase());

        // Step 4: Filter laptops based on matching IDs
        List<Laptop> matchingLaptops = filterLaptopsByIds(laptops, matchingLaptopIds);

        // Return as a list of lists (you can add more lists if needed for other results)
        List<List<Laptop>> result = new ArrayList<>();
        result.add(matchingLaptops);
        return result;
    }

    /**
     * Search for laptops using only the inverted index.
     *
     * @param searchTerm The term to search for.
     * @return A list of laptops matching the prefix in the inverted index.
     */
    @GetMapping("/searchByInvertedIndex")
    public List<Laptop> searchByInvertedIndex(@RequestParam String searchTerm) {
        // Use VocabularyService to fetch relevant laptops first
        List<Laptop> laptops = vocabularyService.searchAndSuggestClosestMatches(searchTerm);

        // Build the inverted index
        buildInvertedIndex(laptops);

        // Perform prefix search
        Set<Long> matchingLaptopIds = invertedIndexService.searchByPrefix(searchTerm.toLowerCase());

        // Filter laptops based on the results
        return filterLaptopsByIds(laptops, matchingLaptopIds);
    }

    /**
     * Builds the inverted index by indexing each word in the laptop fields.
     *
     * @param laptops The list of laptops to index.
     */
    private void buildInvertedIndex(List<Laptop> laptops) {
        for (Laptop laptop : laptops) {
            invertedIndexService.indexLaptop(laptop);
        }
    }

    /**
     * Filters laptops based on a set of matching laptop IDs.
     *
     * @param laptops The list of laptops to filter from.
     * @param matchingIds The set of matching IDs from the inverted index.
     * @return A filtered list of laptops.
     */
    private List<Laptop> filterLaptopsByIds(List<Laptop> laptops, Set<Long> matchingIds) {
        List<Laptop> filteredLaptops = new ArrayList<>();
        for (Laptop laptop : laptops) {
            if (matchingIds.contains(laptop.getId())) {
                filteredLaptops.add(laptop);
            }
        }
        return filteredLaptops;
    }
}
