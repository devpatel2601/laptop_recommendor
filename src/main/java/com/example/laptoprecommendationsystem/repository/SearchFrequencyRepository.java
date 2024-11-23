package com.example.laptoprecommendationsystem.repository;

import com.example.laptoprecommendationsystem.model.SearchFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SearchFrequencyRepository extends JpaRepository<SearchFrequency, Long> {
    Optional<SearchFrequency> findBySearchTerm(String searchTerm);
}
