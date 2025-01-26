package com.xmerca.authservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponseDto {
    private final String accessToken;
}
