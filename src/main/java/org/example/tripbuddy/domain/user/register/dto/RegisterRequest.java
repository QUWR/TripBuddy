package org.example.tripbuddy.domain.user.register.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.tripbuddy.domain.user.domain.User;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @NotBlank(message = "사용자 이름은 필수 입력 항목입니다.")
    private String username;

    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    private String nickname;

    // 이 생성자는 현재 회원가입 로직에서 사용되지 않으므로,
    // 혼동을 피하기 위해 제거하거나 주석 처리하는 것이 좋습니다.
    // public RegisterRequest(User user) { ... }
}
