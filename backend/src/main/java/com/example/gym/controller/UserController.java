package com.example.gym.controller;

import com.example.gym.model.User;
import com.example.gym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody User user, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Validation failed: " + bindingResult.getFieldError().getDefaultMessage());
                errorResponse.put("error", bindingResult.getFieldError().getCode());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            System.out.println("Processing signup for user: " + user.getUsername());
            User newUser = userService.signup(user);
            System.out.println("User saved successfully with ID: " + newUser.getId());
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Signup failed: " + e.getMessage());
            errorResponse.put("error", e.getClass().getSimpleName());
            System.err.println("Signup error: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody User user, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Validation failed: " + bindingResult.getFieldError().getDefaultMessage());
                errorResponse.put("error", bindingResult.getFieldError().getCode());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            System.out.println("Processing signin for user: " + user.getUsername());
            User existingUser = userService.signin(user.getUsername());
            
            if (existingUser != null && userService.checkPassword(user.getPassword(), existingUser.getPassword())) {
                System.out.println("Signin successful for user: " + existingUser.getUsername());
                return ResponseEntity.ok(existingUser);
            }
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid username or password");
            errorResponse.put("error", "INVALID_CREDENTIALS");
            System.out.println("Signin failed - invalid credentials for user: " + user.getUsername());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Sign in failed: " + e.getMessage());
            errorResponse.put("error", e.getClass().getSimpleName());
            System.err.println("Signin error: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
