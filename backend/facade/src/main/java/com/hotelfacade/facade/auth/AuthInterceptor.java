package com.hotelfacade.facade.auth;

import com.hotelfacade.facade.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Rejects requests without a valid "Authorization: Bearer <token>" header. */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String USERNAME_ATTRIBUTE = "username";
    public static final String TOKEN_ATTRIBUTE = "token";

    private final TokenStore tokens;

    public AuthInterceptor(TokenStore tokens) {
        this.tokens = tokens;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true; // CORS pre-flight
        }
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Missing Authorization: Bearer <token> header");
        }
        String token = header.substring(7).trim();
        String username = tokens.resolve(token)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid or expired token"));
        request.setAttribute(USERNAME_ATTRIBUTE, username);
        request.setAttribute(TOKEN_ATTRIBUTE, token);
        return true;
    }
}
