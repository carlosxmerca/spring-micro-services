package com.xmerca.transactionsservice.security.service;

import com.xmerca.transactionsservice.config.exceptions.UnauthorizedException;
import com.xmerca.transactionsservice.security.payload.AuthTokenPayload;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthTokenService {
    private final JwtService jwtService;

    public AuthTokenService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String generateToken(AuthTokenPayload tokenPayload) {
        AuthTokenPayload finalPayload = AuthTokenPayload.builder()
                .userId(tokenPayload.getUserId())
                .role(tokenPayload.getRole())
                .build();
        validatePayload(finalPayload);

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", tokenPayload.getUserId());
        addClaims(claims, tokenPayload);

        return jwtService.generateToken(claims);

    }

    public AuthTokenPayload validateToken(String token) {
        try {
            if (jwtService.isTokenExpired(token))
                throw new UnauthorizedException("Invalid token");

            return extractPayload(token);

        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    protected void validatePayload(AuthTokenPayload payload) {
        if (payload.getUserId() == null || payload.getRole() == null || payload.getRole().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token payload invalid");
    }

    protected void addClaims(Map<String, Object> claims, AuthTokenPayload payload) {
        claims.put("userId", payload.getUserId());
        claims.put("role", payload.getRole());
    }

    protected AuthTokenPayload extractPayload(String token) {
        Claims claims = jwtService.parseToken(token);

        return AuthTokenPayload.builder()
                .userId(claims.get("userId", String.class))
                .role(claims.get("role", String.class))
                .build();
    }

    public boolean isTokenValid(String token, String userId) {
        return (jwtService.extractSubject(token).equals(userId)) && !jwtService.isTokenExpired(token);
    }
}
