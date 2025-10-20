package com.example.gym.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;

public class SignupRequest {
    @NotBlank(message = "Username cannot be blank")
    private String username;
    
    @NotBlank(message = "Password cannot be blank")
    private String password;
    
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Phone cannot be blank")
    private String phone;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
