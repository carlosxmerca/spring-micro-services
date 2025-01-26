package com.xmerca.bankaccountsservice.security.service;

import com.xmerca.bankaccountsservice.config.exceptions.UnauthorizedException;
import com.xmerca.bankaccountsservice.security.payload.AuthTokenPayload;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthTokenService {
    private final JwtService jwtService;

    public AuthTokenService(JwtService jwtService) {
        this.jwtService = jwtService;
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
