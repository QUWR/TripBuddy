package org.example.tripbuddy.global.exception.controller;


import lombok.extern.slf4j.Slf4j;
import org.example.tripbuddy.global.exception.CustomException;
import org.example.tripbuddy.global.exception.ErrorCode;
import org.example.tripbuddy.global.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리를 담당하는 클래스
 * @RestControllerAdvice를 통해 모든 RestController에서 발생하는 예외를 중앙에서 처리
 */
@Slf4j
@RestControllerAdvice   //반환값이 자동으로 responsebody 에 들어감
public class GlobalExceptionHandler {

  /**
   * NullPointerException 발생 시 처리하는 핸들러
   * @return 클라이언트에게 반환할 에러 메시지 문자열
   */
  @ExceptionHandler(NullPointerException.class)
  public String handleNullPointerException() {
    log.error("NullPointer Exception 처리시작");
    return "NullPointer Exception 핸들링";
  }

  /**
   * InternalError 발생 시 처리하는 핸들러
   * @return 클라이언트에게 반환할 에러 메시지 문자열
   */
  @ExceptionHandler(InternalError.class)
  public String handelInternalError() {
    log.error("Internal Error 처리시작");
    return "Internal Error 핸들링";
  }

  /**
   * 직접 정의한 CustomException을 처리하는 핸들러
   * 예외에 포함된 ErrorCode를 기반으로 체계적인 ErrorResponse를 생성하여 반환
   * @param e 발생한 CustomException 객체
   * @return ErrorCode의 상태 코드와 정보를 담은 ResponseEntity<ErrorResponse> 객체
   */
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException e){
    log.error("CustomException : {}", e.getMessage());
    ErrorCode errorCode = e.getErrorCode();

    ErrorResponse response = ErrorResponse.builder()
        .code(errorCode)
        .message(errorCode.getMessage())
        .build();
    return ResponseEntity.status(errorCode.getStatus()).body(response);
  }
}