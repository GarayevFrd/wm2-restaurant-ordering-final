package com.restaurant.ordering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.restaurant.ordering.Repository")
@EntityScan(basePackages = "com.restaurant.ordering.Model")
public class OrderingApplication {
	public static void main(String[] args) {
		SpringApplication.run(OrderingApplication.class, args);
	}
}
