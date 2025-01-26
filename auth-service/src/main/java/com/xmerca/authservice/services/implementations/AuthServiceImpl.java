package com.xmerca.authservice.services.implementations;

import com.xmerca.authservice.security.payload.AuthTokenPayload;
import com.xmerca.authservice.config.exceptions.BadRequestException;
import com.xmerca.authservice.dtos.AuthenticationResponseDto;
import com.xmerca.authservice.dtos.RegisterUserDto;
import com.xmerca.authservice.models.Role;
import com.xmerca.authservice.models.UserModel;
import com.xmerca.authservice.repository.RoleRepository;
import com.xmerca.authservice.repository.UserRepository;
import com.xmerca.authservice.services.AuthService;
import com.xmerca.authservice.security.service.AuthTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, AuthTokenService authTokenService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authTokenService = authTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticationResponseDto authenticate(UUID identifier, String password) {
        try {
            // Authenticate user
            UserModel user = userRepository.findById(identifier)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

            // Add custom claims
            AuthTokenPayload payload = AuthTokenPayload.builder()
                    .userId(user.getId().toString())
                    .role(user.getRole().getName())
                    .build();

            // Generate tokens
            String accessToken = authTokenService.generateToken(payload);

            // Build response
            return AuthenticationResponseDto.builder()
                    .accessToken(accessToken)
                    .build();

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(
                    "Invalid credentials"
            );
        } catch (Exception e) {
            log.error("Error during login", e);
            throw new BadCredentialsException(
                    "Login failed: " + e.getMessage()
            );
        }
    }

    @Override
    public UserModel register(RegisterUserDto registerUserDto) {
        Role role = roleRepository.findById(registerUserDto.getRoleId())
                .orElseThrow(() -> new BadRequestException("Role not found"));

        UserModel user = new UserModel();
        user.setPasswordHash(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setRole(role);
        return userRepository.save(user);
    }
}
