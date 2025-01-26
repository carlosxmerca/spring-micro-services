package com.xmerca.transactionsservice.security.service;

import com.xmerca.transactionsservice.security.dto.CustomUserDetails;
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
