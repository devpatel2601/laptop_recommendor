package com.example.laptoprecommendationsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Laptop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String brandName;
    @Column
    private String productName;
    @Column
    private double price;
    @Column(name = "Image", length = 1000)
    private String image;
    @Column
    private String os;
    @Column
    private String processor;
    @Column
    private String graphics;
    @Column
    private String display;
    @Column
    private String memory;
    @Column
    private String storage;
    // Getters and setters for the existing fields
    @Column(name = "FilePath", length = 1000)
    private String filepath;
    @Column
    private Integer rankScore;
}
