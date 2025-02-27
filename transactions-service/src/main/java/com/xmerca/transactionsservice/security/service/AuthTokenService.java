package com.xmerca.transactionsservice.security.service;

import com.xmerca.transactionsservice.config.exceptions.UnauthorizedException;
import com.xmerca.transactionsservice.security.payload.AuthTokenPayload;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

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

    public String getRequestToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
