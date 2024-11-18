package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.model.Laptop;
import com.example.laptoprecommendationsystem.repository.LaptopRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class LaptopService {

    @Autowired
    private LaptopRepository laptopRepository;
    private static final String EXCEL_FILE_PATH = "src/main/resources/products-Excel.xlsx";
    /**
     * Get all laptops with optional sorting based on the field and order.
     * @param sortBy The field by which to sort (e.g., "price", "memory", "processor").
     * @param order The order of sorting, either "asc" or "desc".
     * @return A list of sorted laptops.
     */
    public List<Laptop> getAllLaptops(String sortBy, String order) {
        // Determine the sort direction
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Create the Sort object using the provided sortBy field and sortDirection
        Sort sort = Sort.by(sortDirection, sortBy);

        // Fetch and return the sorted list of laptops
        return laptopRepository.findAll(sort);
    }


    public Laptop getLaptopById(Long id) {
        Optional<Laptop> optionalLaptop = laptopRepository.findById(id);
        return optionalLaptop.orElse(null);
    }
    /**
     * Recommend laptops based on user preferences.
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
    public List<Laptop> recommendLaptops(String brand, String usageType, Double minBudget, Double maxBudget,
                                         String portability, String performanceRequirements, Integer screenSize,
                                         Integer minStorage, Integer minRAM) {

        List<Laptop> laptops = laptopRepository.findAll();
        List<Laptop> recommendedLaptops = new ArrayList<>();

        // Filter laptops based on user input criteria
        for (Laptop laptop : laptops) {
            boolean matches = true;

            // Check if brand matches
            if (brand != null && !brand.isEmpty() && !laptop.getProductName().toLowerCase().contains(brand.toLowerCase())) {
                matches = false;
            }

            // Check if usage type matches
            if (usageType != null && !usageType.isEmpty() && !laptop.getDisplay().toLowerCase().contains(usageType.toLowerCase())) {
                matches = false;
            }

            // Check if the price is within the budget
            if (laptop.getPrice() < minBudget || laptop.getPrice() > maxBudget) {
                matches = false;
            }

            // Check if portability needs are met (lightweight or battery life)
            if (portability != null && !portability.isEmpty()) {
                // Check if laptop is lightweight (assuming weight attribute is added in model)
                // Check if battery life is long (assuming batteryLife attribute is added in model)
                // If you don't have these attributes, you'll need to either add them or refine the check
            }

            // Check performance requirements (heavy tasks or light tasks)
            if (performanceRequirements != null && !performanceRequirements.isEmpty()) {
                if ("heavy".equalsIgnoreCase(performanceRequirements) &&
                        (laptop.getProcessor().contains("i3") || parseMemory(laptop.getMemory()) < 8 || laptop.getGraphics().contains("integrated"))) {
                    matches = false;
                }
                if ("light".equalsIgnoreCase(performanceRequirements) && laptop.getProcessor().contains("i5") && parseMemory(laptop.getMemory()) >= 8) {
                    matches = false;
                }
            }

            // Check if screen size matches
            if (screenSize != null && screenSize > 0 && !laptop.getDisplay().contains(String.valueOf(screenSize))) {
                matches = false;
            }

            // Check if storage and RAM are sufficient
            if (parseStorage(laptop.getStorage()) < minStorage || parseMemory(laptop.getMemory()) < minRAM) {
                matches = false;
            }

            // If all conditions are met, add the laptop to the recommended list
            if (matches) {
                recommendedLaptops.add(laptop);
            }
        }

        return recommendedLaptops;
    }


    /**
     * Helper method to get the cell value as a String.
     * @param cell The cell to read.
     * @return The cell's value as a String.
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }




    /**
     * Helper method to parse memory size from a string (e.g., "8GB" to 8).
     * @param memory The memory string (e.g., "8GB", "16GB").
     * @return The parsed memory value in GB as an integer.
     */
    private int parseMemory(String memory) {
        try {
            return Integer.parseInt(memory.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0; // Default to 0 if parsing fails
        }
    }

    /**
     * Helper method to parse storage size from a string (e.g., "512GB" to 512).
     * @param storage The storage string (e.g., "512GB", "1TB").
     * @return The parsed storage value in GB as an integer.
     */
    private int parseStorage(String storage) {
        try {
            if (storage.contains("TB")) {
                return Integer.parseInt(storage.replaceAll("[^0-9]", "")) * 1024; // Convert TB to GB
            } else {
                return Integer.parseInt(storage.replaceAll("[^0-9]", ""));
            }
        } catch (NumberFormatException e) {
            return 0; // Default to 0 if parsing fails
        }
    }

    public Laptop addLaptop(Laptop laptop) {
        return laptopRepository.save(laptop);
    }


    public Laptop updateLaptop(Long id, Laptop updatedLaptop) {
        // Check if the laptop exists in the database
        Optional<Laptop> optionalLaptop = laptopRepository.findById(id);
        if (optionalLaptop.isPresent()) {
            Laptop existingLaptop = optionalLaptop.get();

            // Update laptop fields
            existingLaptop.setProductName(updatedLaptop.getProductName());
            existingLaptop.setPrice(updatedLaptop.getPrice());
            existingLaptop.setImage(updatedLaptop.getImage());
            existingLaptop.setOs(updatedLaptop.getOs());
            existingLaptop.setProcessor(updatedLaptop.getProcessor());
            existingLaptop.setGraphics(updatedLaptop.getGraphics());
            existingLaptop.setDisplay(updatedLaptop.getDisplay());
            existingLaptop.setMemory(updatedLaptop.getMemory());
            existingLaptop.setStorage(updatedLaptop.getStorage());

            // Save and return the updated laptop
            return laptopRepository.save(existingLaptop);
        } else {
            return null;
        }
    }


    public void deleteLaptop(Long id) {
        laptopRepository.deleteById(id);
    }
}
