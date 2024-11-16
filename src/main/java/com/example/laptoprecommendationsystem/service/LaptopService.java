package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.model.Laptop;
import com.example.laptoprecommendationsystem.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LaptopService {

    @Autowired
    private LaptopRepository laptopRepository;

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
