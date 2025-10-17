package com.example.gym.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    @Value("${spring.datasource.url:NOT_FOUND}")
    private String dbUrl;

    @Value("${spring.datasource.username:NOT_FOUND}")
    private String dbUsername;

    @Value("${server.port:NOT_FOUND}")
    private String serverPort;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("----------------------------------------------------");
        System.out.println("--- Verifying Environment Variables at Startup ---");
        System.out.println("DATABASE URL: " + dbUrl);
        System.out.println("DATABASE USERNAME: " + dbUsername);
        System.out.println("SERVER PORT: " + serverPort);
        System.out.println("----------------------------------------------------");
    }
}
