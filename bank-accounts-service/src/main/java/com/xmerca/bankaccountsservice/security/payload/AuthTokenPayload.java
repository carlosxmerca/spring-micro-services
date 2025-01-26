package com.xmerca.bankaccountsservice.security.payload;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthTokenPayload  {
    private final String userId;
    private final String role;
    private final long expiration;
}
