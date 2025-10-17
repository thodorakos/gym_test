package com.example.gym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GymApplication {

	public static void main(String[] args) {
		System.out.println("--- Verifying Environment Variables before Spring starts ---");
		System.out.println("SPRING_DATASOURCE_URL: " + System.getenv("SPRING_DATASOURCE_URL"));
		System.out.println("SPRING_DATASOURCE_USERNAME: " + System.getenv("SPRING_DATASOURCE_USERNAME"));
		System.out.println("SERVER_PORT: " + System.getenv("SERVER_PORT"));
		System.out.println("----------------------------------------------------");
		SpringApplication.run(GymApplication.class, args);
	}}
