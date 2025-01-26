package com.xmerca.authservice.dtos;

import lombok.Data;

@Data
public class RegisterUserDto {
    private String password;
    private Long roleId;
}
