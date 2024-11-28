package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.model.Laptop;
import com.example.laptoprecommendationsystem.repository.LaptopRepository;
import com.example.laptoprecommendationsystem.util.BoyerMoore;
import com.example.laptoprecommendationsystem.util.MaxHeap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PageRankingService {

    @Autowired
    private LaptopRepository laptopRepository;

    /**
     * Ranks pages based on the total frequency count of the search term.
     * @param searchTerm The search term to rank pages.
     * @param pageSize The number of laptops per page.
     * @return A list of ranked pages with their total frequency count.
     */
    public List<PageRank> getRankedPages(String searchTerm, int pageSize) {
        // Determine total number of pages
        long totalLaptops = laptopRepository.count();
        int totalPages = (int) Math.ceil((double) totalLaptops / pageSize);

        // MaxHeap to store and rank pages
        MaxHeap<PageRank> maxHeap = new MaxHeap<>(Comparator.comparingInt(PageRank::getFrequency).reversed());

        // Iterate over each page to calculate frequency
        for (int page = 0; page < totalPages; page++) {
            List<Laptop> laptops = laptopRepository.findAll(PageRequest.of(page, pageSize)).getContent();

            // Calculate total frequency count for the current page
            int totalFrequency = laptops.stream()
                    .mapToInt(laptop -> countFrequencyUsingBoyerMoore(laptop, searchTerm))
                    .sum();

            // Add the page to the max heap
            maxHeap.add(new PageRank(page, totalFrequency));
        }

        // Extract pages from the max heap in ranked order
        List<PageRank> rankedPages = new ArrayList<>();
        while (maxHeap.size() > 0) {
            rankedPages.add(maxHeap.remove());
        }

        // Sort the ranked pages by frequency in descending order
        rankedPages.sort(Comparator.comparingInt(PageRank::getFrequency).reversed());

        return rankedPages;
    }



    /**
     * Count the frequency of a search term in the given laptop's productName and brandName using Boyer-Moore.
     * @param laptop The laptop object.
     * @param searchTerm The search term to count.
     * @return The frequency count of the search term.
     */
    private int countFrequencyUsingBoyerMoore(Laptop laptop, String searchTerm) {
        String textToSearch = laptop.getProductName() + " " + laptop.getBrandName();  // Combine product name and brand name for searching
        BoyerMoore boyerMoore = new BoyerMoore(searchTerm);  // Initialize Boyer-Moore with the search term
        return boyerMoore.search(textToSearch.toLowerCase());  // Use the Boyer-Moore search
    }

    /**
     * Class to represent a page and its total frequency count.
     */
    public static class PageRank {
        private final int pageNumber;
        private final int frequency;

        public PageRank(int pageNumber, int frequency) {
            this.pageNumber = pageNumber;
            this.frequency = frequency;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public int getFrequency() {
            return frequency;
        }
    }
}
