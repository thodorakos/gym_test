package com.example.gym.controller;

import com.example.gym.model.Session;
import com.example.gym.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostMapping("/book")
    public Session bookSession(@RequestBody Session session) {
        return sessionService.bookSession(session);
    }

    @GetMapping("/user/{userId}")
    public List<Session> getUserSessions(@PathVariable Long userId) {
        return sessionService.getUserSessions(userId);
    }

    @GetMapping("/all")
    public List<Session> getAllSessions() {
        return sessionService.getAllSessions();
    }
}
