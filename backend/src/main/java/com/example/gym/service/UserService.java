package com.example.gym.service;

import com.example.gym.model.User;
import com.example.gym.dto.SignupRequest;
import com.example.gym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User signup(SignupRequest signupRequest) {
        System.out.println("UserService.signup - Processing user: " + signupRequest.getUsername());
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPhone(signupRequest.getPhone());
        user.setRole("USER"); // Default role
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        user.setPassword(encodedPassword);
        System.out.println("UserService.signup - Password encoded, saving user...");
        User savedUser = userRepository.save(user);
        System.out.println("UserService.signup - User saved with ID: " + savedUser.getId());
        return savedUser;
    }

    public User signin(String username) {
        System.out.println("UserService.signin - Looking up user: " + username);
        User user = userRepository.findByUsername(username);
        if (user != null) {
            System.out.println("UserService.signin - User found: " + user.getId());
        } else {
            System.out.println("UserService.signin - User not found");
        }
        return user;
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
