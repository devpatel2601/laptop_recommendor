package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.model.Laptop;
import com.example.laptoprecommendationsystem.repository.LaptopRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    /**
     * Get a paginated list of laptops.
     * @param page The page number to retrieve (0-based index).
     * @param size The number of laptops per page.
     * @return A page of laptops.
     */
    public Page<Laptop> getLaptopsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return laptopRepository.findAll(pageable);
    }

    public Laptop getLaptopById(Long id) {
        Optional<Laptop> optionalLaptop = laptopRepository.findById(id);
        return optionalLaptop.orElse(null);
    }
    /**
     * Recommend laptops based on user preferences.
     * @param brand Preferred brand (e.g., MacBook, Dell, Lenovo)

     * @param minBudget Minimum price
     * @param maxBudget Maximum price

     * @param screenSize Preferred screen size (e.g., 13", 15", 17")
     * @param minStorage Minimum storage in GB
     * @param minRAM Minimum RAM in GB
     * @return A list of recommended laptops based on the criteria.
     */
    public List<Laptop> recommendLaptops(String brand, Double minBudget, Double maxBudget,
                                         Integer screenSize, Integer minStorage, Integer minRAM,
                                         int page, int pageSize) {

        // Create a Pageable object using the page number and page size
        Pageable pageable = PageRequest.of(page, pageSize);

        List<Laptop> laptops = laptopRepository.findAll();
        List<Laptop> filteredLaptops = new ArrayList<>();

        // Filter laptops based on user input criteria
        for (Laptop laptop : laptops) {
            boolean matches = true;

            // Check if brand matches
            if (brand != null && !brand.isEmpty() && !laptop.getBrandName().toLowerCase().contains(brand.toLowerCase())) {
                matches = false;
            }

            // Check if the price is within the budget
            if ((minBudget != null && laptop.getPrice() < minBudget) ||
                    (maxBudget != null && laptop.getPrice() > maxBudget)) {
                matches = false;
            }

            // Check if screen size matches
            if (screenSize != null && screenSize > 0 && !laptop.getDisplay().contains(String.valueOf(screenSize))) {
                matches = false;
            }

            // Check if storage is sufficient
            if (minStorage != null && parseStorage(laptop.getStorage()) < minStorage) {
                matches = false;
            }

            // Check if RAM is sufficient
            if (minRAM != null && parseMemory(laptop.getMemory()) < minRAM) {
                matches = false;
            }

            // If all conditions are met, add the laptop to the filtered list
            if (matches) {
                filteredLaptops.add(laptop);
            }
        }

        // Calculate the start and end indices for pagination
        int start = Math.min((int) pageable.getOffset(), filteredLaptops.size());
        int end = Math.min((start + pageable.getPageSize()), filteredLaptops.size());

        // Return the sublist representing the current page
        return filteredLaptops.subList(start, end);
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
