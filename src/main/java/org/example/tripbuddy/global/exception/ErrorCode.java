package org.example.tripbuddy.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    //회원가입 에러
    ExistingEmail(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다"),
    ExistingNickname(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다"),
    UserNotFound(HttpStatus.NOT_FOUND, "존재하지 않는 유저 입니다.");


    private final HttpStatus status;
    private final String message;

}
