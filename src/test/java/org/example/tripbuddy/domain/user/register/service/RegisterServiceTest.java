package org.example.tripbuddy.domain.user.register.service;

import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.domain.user.register.dto.RegisterRequest;
import org.example.tripbuddy.domain.user.repository.UserRepository;
import org.example.tripbuddy.global.exception.CustomException;
import org.example.tripbuddy.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterService 단위 테스트")
class RegisterServiceTest {

    @InjectMocks
    private RegisterService registerService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @DisplayName("[성공] 회원가입 시 비밀번호는 암호화되어 저장된다")
    void register_ShouldSaveUserWithEncodedPassword_WhenRequestIsValid() {
        // given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setUsername("testuser");
        request.setNickname("tester");

        String encodedPassword = "encodedPassword123";
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByNickname(anyString())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn(encodedPassword);

        // when
        registerService.register(request);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getEmail()).isEqualTo(request.getEmail());
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.getPassword()).isNotEqualTo(request.getPassword());
    }

    @Test
    @DisplayName("[실패] 이미 존재하는 이메일로 가입 시 ExistingEmail 예외가 발생한다")
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");

        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> registerService.register(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ExistingEmail);
    }
}
