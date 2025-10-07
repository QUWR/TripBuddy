package org.example.tripbuddy.domain.user.register.dto;


import lombok.Getter;
import org.example.tripbuddy.domain.user.domain.User;

@Getter
public class RegisterResponse {
    private String username;
    private String nickname;
    private String email;

    public RegisterResponse(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
    }
}
