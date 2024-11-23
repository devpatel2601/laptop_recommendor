package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.model.SearchFrequency;
import com.example.laptoprecommendationsystem.repository.SearchFrequencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SearchFrequencyService {

    @Autowired
    private SearchFrequencyRepository searchFrequencyRepository;

    // Increment the search count for a given search term
    @Transactional
    public void incrementSearchCount(String searchTerm) {
        searchTerm = searchTerm.toLowerCase();  // Normalize the search term to lower case
        Optional<SearchFrequency> existingSearch = searchFrequencyRepository.findBySearchTerm(searchTerm);

        if (existingSearch.isPresent()) {
            // If the search term exists, increment its count
            SearchFrequency searchFrequency = existingSearch.get();
            searchFrequency.setSearchCount(searchFrequency.getSearchCount() + 1);
            searchFrequencyRepository.save(searchFrequency);
        } else {
            // If the search term does not exist, add it with a count of 1
            SearchFrequency newSearchFrequency = new SearchFrequency();
            newSearchFrequency.setSearchTerm(searchTerm);
            newSearchFrequency.setSearchCount(1);
            searchFrequencyRepository.save(newSearchFrequency);
        }
    }

    // Retrieve the count of a specific search term
    public int getSearchCount(String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        Optional<SearchFrequency> searchFrequency = searchFrequencyRepository.findBySearchTerm(searchTerm);
        return searchFrequency.map(SearchFrequency::getSearchCount).orElse(0);
    }
}
