package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.model.Laptop;
import com.example.laptoprecommendationsystem.service.LaptopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laptops")
public class LaptopController {

    @Autowired
    private LaptopService laptopService;
    /**
     * Get recommended laptops based on user input preferences.
     * @param brand Preferred brand (e.g., MacBook, Dell, Lenovo)
     * @param usageType Preferred usage (e.g., gaming, productivity, graphic design, casual browsing)
     * @param minBudget Minimum price
     * @param maxBudget Maximum price
     * @param portability Lightweight or long battery life
     * @param performanceRequirements Heavy tasks (e.g., gaming, video editing) or light tasks
     * @param screenSize Preferred screen size (e.g., 13", 15", 17")
     * @param minStorage Minimum storage in GB
     * @param minRAM Minimum RAM in GB
     * @return A list of recommended laptops based on the criteria.
     */
    @GetMapping("/recommend")
    public List<Laptop> recommendLaptops(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String usageType,
            @RequestParam(required = false) Double minBudget,
            @RequestParam(required = false) Double maxBudget,
            @RequestParam(required = false) String portability,
            @RequestParam(required = false) String performanceRequirements,
            @RequestParam(required = false) Integer screenSize,
            @RequestParam(required = false) Integer minStorage,
            @RequestParam(required = false) Integer minRAM) {

        return laptopService.recommendLaptops(brand, usageType, minBudget, maxBudget, portability,
                performanceRequirements, screenSize, minStorage, minRAM);
    }
    /**
     * Retrieve all laptops with optional sorting.
     * @param sortBy The field to sort by (e.g., "price", "memory", "processor").
     * @param order The sort order, either "asc" or "desc".
     * @return A list of sorted laptops.
     */
    @GetMapping
    public ResponseEntity<List<Laptop>> getAllLaptops(
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String order) {

        List<Laptop> laptops = laptopService.getAllLaptops(sortBy, order);
        return new ResponseEntity<>(laptops, HttpStatus.OK);
    }

    /**
     * Retrieve a laptop by its ID.
     * @param id The ID of the laptop to retrieve.
     * @return The laptop if found, otherwise a 404 status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Laptop> getLaptopById(@PathVariable Long id) {
        Laptop laptop = laptopService.getLaptopById(id);
        if (laptop != null) {
            return new ResponseEntity<>(laptop, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Add a new laptop.
     * @param laptop The laptop to be added.
     * @return The added laptop with a CREATED status.
     */
    @PostMapping
    public ResponseEntity<Laptop> addLaptop(@RequestBody Laptop laptop) {
        Laptop savedLaptop = laptopService.addLaptop(laptop);
        return new ResponseEntity<>(savedLaptop, HttpStatus.CREATED);
    }

    /**
     * Update an existing laptop.
     * @param id The ID of the laptop to update.
     * @param laptop The updated laptop details.
     * @return The updated laptop if the ID exists, otherwise a 404 status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Laptop> updateLaptop(
            @PathVariable Long id,
            @RequestBody Laptop laptop) {

        Laptop updatedLaptop = laptopService.updateLaptop(id, laptop);
        if (updatedLaptop != null) {
            return new ResponseEntity<>(updatedLaptop, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete a laptop by its ID.
     * @param id The ID of the laptop to delete.
     * @return A NO_CONTENT status if deletion is successful.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLaptop(@PathVariable Long id) {
        laptopService.deleteLaptop(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
