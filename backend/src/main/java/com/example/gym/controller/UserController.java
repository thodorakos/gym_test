package com.example.gym.controller;

import com.example.gym.model.User;
import com.example.gym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody User user, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>("Validation failed: " + bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }
            User newUser = userService.signup(user);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Signup failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody User user, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>("Validation failed: " + bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }
            User existingUser = userService.signin(user.getUsername());
            if (existingUser != null && userService.checkPassword(user.getPassword(), existingUser.getPassword())) {
                return ResponseEntity.ok(existingUser);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            return new ResponseEntity<>("Sign in failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
