package com.wizeflow.crm.backend.security.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlocklistService {

    // map token -> expiry epoch milli
    private final Map<String, Long> blocklist = new ConcurrentHashMap<>();

    public void blockToken(String token, Instant expiresAt) {
        if (token == null || expiresAt == null) return;
        blocklist.put(token, expiresAt.toEpochMilli());
    }

    public boolean isBlocked(String token) {
        if (token == null) return false;
        Long expiry = blocklist.get(token);
        if (expiry == null) return false;
        if (expiry < Instant.now().toEpochMilli()) {
            blocklist.remove(token);
            return false;
        }
        return true;
    }
}

