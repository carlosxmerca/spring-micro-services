package com.xmerca.authservice.controllers;

import com.xmerca.authservice.dtos.AuthenticationResponseDto;
import com.xmerca.authservice.dtos.LoginUserDto;
import com.xmerca.authservice.dtos.RegisterUserDto;
import com.xmerca.authservice.models.UserModel;
import com.xmerca.authservice.repository.UserRepository;
import com.xmerca.authservice.security.dto.CustomUserDetails;
import com.xmerca.authservice.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@Valid @RequestBody LoginUserDto loginRequest) {
        return ResponseEntity.ok(authService.authenticate(loginRequest.getIdentifier(), loginRequest.getPassword()));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserModel> registerUser(@Valid @RequestBody RegisterUserDto user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @GetMapping("/me")
    public ResponseEntity<UserModel> whoAmI(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        try {
            log.info("Current user id: {}", currentUser.getUsername());
            log.info("Current user role: {}", currentUser.getRole());

            UserModel user = userRepository.findById(UUID.fromString(currentUser.getUsername()))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            // This catches invalid UUID format, but should never happen with proper authentication
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication token");
        }
    }
}
