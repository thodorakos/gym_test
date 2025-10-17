package com.example.gym.service;

import com.example.gym.model.Session;
import com.example.gym.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public Session bookSession(Session session) {
        return sessionRepository.save(session);
    }

    public List<Session> getUserSessions(Long userId) {
        return sessionRepository.findByUserId(userId);
    }

    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }
}
