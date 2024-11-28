package com.example.laptoprecommendationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class LaptopRecommendationSystemApplication {

	public static void 	main(String[] args) {
		SpringApplication.run(LaptopRecommendationSystemApplication.class, args);
	}

	// Define RestTemplate as a Bean in the main application class
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();  // Creates a RestTemplate bean
	}

}
