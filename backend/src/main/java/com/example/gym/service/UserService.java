package com.example.gym.service;

import com.example.gym.model.User;
import com.example.gym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User signup(User user) {
        user.setRole("USER"); // Default role
        return userRepository.save(user);
    }

    public User signin(User user) {
        return userRepository.findByUsername(user.getUsername());
    }
}
