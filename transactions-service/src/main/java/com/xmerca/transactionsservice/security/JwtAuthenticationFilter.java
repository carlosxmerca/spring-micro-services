package com.xmerca.transactionsservice.security;

import com.xmerca.transactionsservice.config.exceptions.UnauthorizedException;
import com.xmerca.transactionsservice.security.dto.CustomUserDetails;
import com.xmerca.transactionsservice.security.payload.AuthTokenPayload;
import com.xmerca.transactionsservice.security.service.AuthTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenService authTokenService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException, UnauthorizedException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userIdentifier;
        final String userRole;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwt = authHeader.substring(7);
            AuthTokenPayload payload = authTokenService.validateToken(jwt);
            userIdentifier = payload.getUserId();
            userRole = payload.getRole();

            if (userIdentifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (authTokenService.isTokenValid(jwt, userIdentifier)) {
                    UserDetails userDetails = new CustomUserDetails(userIdentifier, userRole);
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException e) {
            log.error(e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(String.format("{\"error\": \"%s\"}", e.getMessage()));
        }
    }
}
