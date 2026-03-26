package com.financetracker.userservice.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private Long userId;
    private String message;

    public AuthResponse(String token, String name, String email, Long userId, String message) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.message = message;
    }
}
