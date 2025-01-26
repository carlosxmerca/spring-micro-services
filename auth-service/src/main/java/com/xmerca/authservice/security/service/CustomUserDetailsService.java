package com.xmerca.authservice.security.service;

import com.xmerca.authservice.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public CustomUserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        try {
            UUID uuid = UUID.fromString(identifier);
            return new CustomUserDetails(uuid.toString(), "");
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
