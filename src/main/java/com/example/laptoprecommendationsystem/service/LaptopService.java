package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.model.Laptop;
import com.example.laptoprecommendationsystem.repository.LaptopRepository;
import com.example.laptoprecommendationsystem.util.LaptopQuickSort;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LaptopService {

    @Autowired
    private LaptopRepository laptopRepository;


//    private static final String EXCEL_FILE_PATH = "src/main/resources/products-Excel-upd.xlsx";


    public List<Laptop> getAllLaptopsSortedByPrice(String order) {
        // Fetch all laptops from the repository
        List<Laptop> laptops = laptopRepository.findAll();

        // Sort laptops by price using QuickSort
        LaptopQuickSort.quickSortByPrice(laptops, order);

        return laptops;
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
    // Method to filter laptops based on given criteria
    private List<Laptop> applyFilters(String brand, Double minBudget, Double maxBudget, Integer screenSize, Integer minStorage, Integer minRAM) {
        // Get all laptops from the database
        List<Laptop> allLaptops = laptopRepository.findAll();
        List<Laptop> filteredLaptops = new ArrayList<>();

        // Iterate over each laptop and check if it matches the filters
        for (Laptop laptop : allLaptops) {
            boolean matches = true;

            // Check brand
            if (brand != null && !brand.isEmpty() && !laptop.getBrandName().toLowerCase().contains(brand.toLowerCase())) {
                matches = false;
            }

            // Check minimum and maximum budget
            if ((minBudget != null && laptop.getPrice() < minBudget) ||
                    (maxBudget != null && laptop.getPrice() > maxBudget)) {
                matches = false;
            }

            // Check screen size
            if (screenSize != null && screenSize > 0 && !laptop.getDisplay().contains(String.valueOf(screenSize))) {
                matches = false;
            }

            // Check minimum storage
            if (minStorage != null && parseStorage(laptop.getStorage()) < minStorage) {
                matches = false;
            }

            // Check minimum RAM
            if (minRAM != null && parseMemory(laptop.getMemory()) < minRAM) {
                matches = false;
            }

            // If all conditions are met, add to the filtered list
            if (matches) {
                filteredLaptops.add(laptop);
            }
        }

        return filteredLaptops;
    }



    // Method to get filtered laptops with pagination
    public Page<Laptop> getRecommendedLaptops(String brand, Double minBudget, Double maxBudget, Integer screenSize, Integer minStorage, Integer minRAM, Pageable pageable) {
        // Regex pattern to match numeric values
        Pattern numericPattern = Pattern.compile("^\\d+(\\.\\d+)?$");

        // Validate that budget and RAM values are numeric
        if ((minBudget != null && !numericPattern.matcher(minBudget.toString()).matches()) ||
                (maxBudget != null && !numericPattern.matcher(maxBudget.toString()).matches()) ||
                (minRAM != null && !numericPattern.matcher(minRAM.toString()).matches()) ||
                (screenSize != null && !numericPattern.matcher(screenSize.toString()).matches())) {

            throw new IllegalArgumentException("Input values for budget, RAM, and screen size must be numeric.");
        }

        // Apply filters after validation
        List<Laptop> filteredLaptops = applyFilters(brand, minBudget, maxBudget, screenSize, minStorage, minRAM);

        // Convert the filtered list to a pageable format
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredLaptops.size());
        List<Laptop> paginatedLaptops = filteredLaptops.subList(start, end);

        return new PageImpl<>(paginatedLaptops, pageable, filteredLaptops.size());
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


    public List<Laptop> getSuggestions(String query, int page, int size) {
        // Create a PageRequest object for pagination
        PageRequest pageRequest = PageRequest.of(page, size);

        // Fetch suggestions based on productName
        return laptopRepository.findByBrandNameContainingIgnoreCase(query, pageRequest);
    }

    /**
     * Helper method to parse memory size from a string (e.g., "8GB" to 8).
     * @param memory The memory string (e.g., "8GB", "16GB").
     * @return The parsed memory value in GB as an integer.
     */
    private int parseMemory(String memory) {
        try {
            // Use a regular expression to find the first numeric value in the string
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(memory);

            // If a number is found, return it as an integer
            if (matcher.find()) {
                return Integer.parseInt(matcher.group());
            }
        } catch (NumberFormatException e) {
            // Return 0 if parsing fails
        }

        return 0; // Default to 0 if no numeric value is found
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

    // Search laptops based on a search term in brand name or product name
    public List<Laptop> searchLaptops(String searchTerm) {
        // Fetch laptops by product name or brand name containing the search term
        return laptopRepository.findByProductNameContainingIgnoreCaseOrBrandNameContainingIgnoreCase(searchTerm, searchTerm);
    }
}

