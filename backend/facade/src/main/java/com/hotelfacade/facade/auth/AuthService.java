package com.hotelfacade.facade.auth;

import com.hotelfacade.facade.exception.ApiException;
import com.hotelfacade.facade.model.AppUser;
import com.hotelfacade.facade.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository users;
    private final TokenStore tokens;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(AppUserRepository users, TokenStore tokens) {
        this.users = users;
        this.tokens = tokens;
    }

    public void register(String username, String password) {
        String name = username.trim().toLowerCase();
        if (users.existsByUsername(name)) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already taken");
        }
        users.save(new AppUser(name, encoder.encode(password)));
    }

    /** Returns a session token when the credentials are valid. */
    public String login(String username, String password) {
        AppUser user = users.findByUsername(username.trim().toLowerCase())
                .filter(u -> encoder.matches(password, u.getPasswordHash()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        return tokens.issue(user.getUsername());
    }

    public void logout(String token) {
        tokens.revoke(token);
    }
}
