package org.example.tripbuddy.domain.user.register.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tripbuddy.global.exception.CustomException;
import org.example.tripbuddy.global.exception.ErrorCode;
import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.domain.user.register.dto.RegisterRequest;
import org.example.tripbuddy.domain.user.register.dto.RegisterResponse;
import org.example.tripbuddy.domain.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public RegisterResponse register(RegisterRequest request){

        //중복 검증
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("이미 사용중인 이메일입니다. 이메일: {}", request.getEmail());
            throw new CustomException(ErrorCode.ExistingEmail);
        }

        //새로운 회원 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .username(request.getUsername())
                .nickname(request.getNickname())
                .build();

        //회원 저장
        userRepository.save(user);

        log.info("회원가입 완료. user={}", user.toString());
        return new RegisterResponse(user);

    }

}
