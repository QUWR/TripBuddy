package org.example.tripbuddy.domain.user.login.dto;


import lombok.Getter;
import org.example.tripbuddy.domain.user.domain.User;

@Getter
public class LoginResponse {

    private String username;
    private String nickname;

    public LoginResponse(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
    }
}
