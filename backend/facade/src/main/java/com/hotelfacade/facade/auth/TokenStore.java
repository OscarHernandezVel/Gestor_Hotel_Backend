package com.hotelfacade.facade.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory session tokens (cleared when the server restarts). */
@Component
public class TokenStore {

    private record Session(String username, Instant expiresAt) {
    }

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final long ttlMinutes;

    public TokenStore(@Value("${app.auth.token-ttl-minutes:480}") long ttlMinutes) {
        this.ttlMinutes = ttlMinutes;
    }

    public String issue(String username) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sessions.put(token, new Session(username, Instant.now().plusSeconds(ttlMinutes * 60)));
        return token;
    }

    public Optional<String> resolve(String token) {
        Session s = sessions.get(token);
        if (s == null) {
            return Optional.empty();
        }
        if (s.expiresAt().isBefore(Instant.now())) {
            sessions.remove(token);
            return Optional.empty();
        }
        return Optional.of(s.username());
    }

    public void revoke(String token) {
        sessions.remove(token);
    }
}
