package org.example.tripbuddy.domain.user.register.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.tripbuddy.domain.user.domain.User;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    private String email;
    private String password;
    private String username;
    private String nickname;

    public RegisterRequest(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
    }
}
