package com.xmerca.authservice.services;

import com.xmerca.authservice.dtos.AuthenticationResponseDto;
import com.xmerca.authservice.dtos.RegisterUserDto;
import com.xmerca.authservice.models.UserModel;
import java.util.UUID;

public interface AuthService {
    AuthenticationResponseDto authenticate(UUID identifier, String password);
    UserModel register(RegisterUserDto registerUserDto);
}
