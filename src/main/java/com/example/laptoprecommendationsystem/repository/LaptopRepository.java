package com.example.laptoprecommendationsystem.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.laptoprecommendationsystem.model.Laptop;
import java.util.List;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop, Long> {

    // Use Sort to dynamically order results
    List<Laptop> findAll(Sort sort);
}
