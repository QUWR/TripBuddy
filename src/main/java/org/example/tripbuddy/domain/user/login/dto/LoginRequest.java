package org.example.tripbuddy.domain.user.login.dto;


import lombok.Getter;

@Getter
public class LoginRequest {

    private String email;
    private String password;
}
