package com.example.gym.config;

import com.example.gym.model.User;
import com.example.gym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // Check if admin user already exists
            User existingAdmin = userRepository.findByUsername("admin");
            if (existingAdmin == null) {
                // Create admin user
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setEmail("admin@apexgym.com");
                admin.setPhone("000-000-0000");
                admin.setRole("ADMIN");
                userRepository.save(admin);
                System.out.println("Admin user created: username=admin, password=admin (encoded)");
            } else {
                // Update existing admin with encoded password to ensure it's correct
                // This handles the case where the password was stored as plain text
                existingAdmin.setPassword(passwordEncoder.encode("admin"));
                existingAdmin.setEmail("admin@apexgym.com");
                existingAdmin.setPhone("000-000-0000");
                existingAdmin.setRole("ADMIN");
                userRepository.save(existingAdmin);
                System.out.println("Admin user updated with encoded password");
            }
        };
    }
}
