package com.example.gym.config;

import com.example.gym.model.User;
import com.example.gym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // Check if admin user already exists
            User existingAdmin = userRepository.findByUsername("admin");
            if (existingAdmin == null) {
                // Create admin user
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin");
                admin.setEmail("admin@apexgym.com");
                admin.setPhone("000-000-0000");
                admin.setRole("ADMIN");
                userRepository.save(admin);
                System.out.println("Admin user created: username=admin, password=admin");
            } else {
                System.out.println("Admin user already exists");
            }
        };
    }
}
