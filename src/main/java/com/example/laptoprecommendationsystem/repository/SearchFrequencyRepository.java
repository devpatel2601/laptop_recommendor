package com.example.laptoprecommendationsystem.repository;

import com.example.laptoprecommendationsystem.model.SearchFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchFrequencyRepository extends JpaRepository<SearchFrequency, Long> {

    // Query to get most frequent search queries
    @Query("SELECT s.query, COUNT(s.query) FROM SearchFrequency s GROUP BY s.query ORDER BY COUNT(s.query) DESC")
    List<String> findMostFrequentSearchTerms();
}
