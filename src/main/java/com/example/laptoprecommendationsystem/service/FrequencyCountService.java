package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.model.Laptop;
import com.example.laptoprecommendationsystem.repository.LaptopRepository;
import com.example.laptoprecommendationsystem.util.BoyerMoore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FrequencyCountService {

    @Autowired
    private LaptopRepository laptopRepository;

    /**
     * Get the total frequency count of the search term in all laptops on the page using Boyer-Moore algorithm.
     * @param searchTerm The word to search for in laptops.
     * @param page The page number for pagination.
     * @param size The number of laptops per page.
     * @return The total frequency count of the search term in the productName and brandName of all laptops on the page.
     */
    public int getTotalFrequencyCount(String searchTerm, int page, int size) {
        // Fetch laptops for the given page
        List<Laptop> laptops = laptopRepository.findAll(PageRequest.of(page, size)).getContent();

        // Sum up the frequency count for the search term across all laptops
        return laptops.stream()
                .mapToInt(laptop -> countFrequencyUsingBoyerMoore(laptop, searchTerm))
                .sum();  // Sum the frequencies for the entire page
    }

    /**
     * Count the frequency of a search term in the given laptop's productName and brandName using Boyer-Moore.
     * @param laptop The laptop object.
     * @param searchTerm The term to search.
     * @return The frequency count of the search term.
     */
    private int countFrequencyUsingBoyerMoore(Laptop laptop, String searchTerm) {
        String textToSearch = laptop.getProductName() + " " + laptop.getBrandName();  // Combine product name and brand name for searching
        BoyerMoore boyerMoore = new BoyerMoore(searchTerm);  // Initialize Boyer-Moore with the search term
        return boyerMoore.search(textToSearch.toLowerCase());  // Use the Boyer-Moore search
    }
}
