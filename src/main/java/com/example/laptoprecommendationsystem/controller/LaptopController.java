package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.model.Laptop;
import com.example.laptoprecommendationsystem.service.LaptopService;
import com.example.laptoprecommendationsystem.service.PageRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/laptops")
public class LaptopController {

    @Autowired
    private PageRankingService pageRankingService;


    @Autowired
    private LaptopService laptopService;
    /**
     * Get recommended laptops based on user input preferences.
     * @param brand Preferred brand (e.g., MacBook, Dell, Lenovo)
     * @param ram Minimum price
     * @param page Maximum price
     * @param storage Lightweight or long battery life
     * @param maxPrice Heavy tasks (e.g., gaming, video editing) or light tasks
     * @param screenSize Preferred screen size (e.g., 13", 15", 17")
     * @param minPrice Minimum storage in GB
     * @param size Minimum RAM in GB
     * @return A list of recommended laptops based on the criteria.
     */
    @GetMapping("/recommend")
    public Page<Laptop> getRecommendedLaptops(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer screenSize,
            @RequestParam(required = false) Integer storage,
            @RequestParam(required = false) Integer ram,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return laptopService.getRecommendedLaptops(brand, minPrice, maxPrice, screenSize, storage, ram, pageable);
    }


    @GetMapping("/sorted-by-price")
    public List<Laptop> getLaptopsSortedByPrice(@RequestParam String order) {
        return laptopService.getAllLaptopsSortedByPrice(order);
    }




    // Endpoint to get search suggestions based on the query
    @GetMapping("/suggestions")
    public List<Laptop> getLaptopSuggestions(@RequestParam String query,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "5") int size) {
        // Query is the search term, page is the page number, and size is the number of results per page
        return laptopService.getSuggestions(query, page, size);
    }

    // API to search laptops based on a search term
    @GetMapping("/search")
    public ResponseEntity<List<Laptop>> searchLaptops(@RequestParam String searchTerm) {
        List<Laptop> laptops = laptopService.searchLaptops(searchTerm);
        return new ResponseEntity<>(laptops, HttpStatus.OK);
    }

    /**
     * Get paginated laptops.
     * @param page The page number (0-based index).
     * @param size The number of laptops per page.
     * @return A page of laptops.
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<Laptop>> getLaptopsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Laptop> laptopsPage = laptopService.getLaptopsPaginated(page, size);
        return ResponseEntity.ok(laptopsPage);
    }
}
