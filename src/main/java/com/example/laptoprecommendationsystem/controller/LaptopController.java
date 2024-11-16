package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.model.Laptop;
import com.example.laptoprecommendationsystem.service.LaptopService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
