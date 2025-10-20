package com.example.gym.controller;

import com.example.gym.model.Session;
import com.example.gym.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostMapping("/book")
    public ResponseEntity<?> bookSession(@RequestBody Session session) {
        try {
            System.out.println("BookSession - Received session: " + session.getSessionType() + ", User ID: " + (session.getUser() != null ? session.getUser().getId() : "null"));
            if (session.getUser() == null || session.getUser().getId() == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "User ID is required");
                errorResponse.put("error", "MISSING_USER_ID");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            Session bookedSession = sessionService.bookSession(session);
            System.out.println("BookSession - Session saved with ID: " + bookedSession.getId());
            return new ResponseEntity<>(bookedSession, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to book session: " + e.getMessage());
            errorResponse.put("error", e.getClass().getSimpleName());
            System.err.println("BookSession error: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserSessions(@PathVariable Long userId) {
        try {
            System.out.println("GetUserSessions - Getting sessions for user: " + userId);
            List<Session> sessions = sessionService.getUserSessions(userId);
            System.out.println("GetUserSessions - Found " + sessions.size() + " sessions");
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to get sessions: " + e.getMessage());
            errorResponse.put("error", e.getClass().getSimpleName());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSessions() {
        try {
            System.out.println("GetAllSessions - Getting all sessions");
            List<Session> sessions = sessionService.getAllSessions();
            System.out.println("GetAllSessions - Found " + sessions.size() + " sessions");
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to get all sessions: " + e.getMessage());
            errorResponse.put("error", e.getClass().getSimpleName());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
