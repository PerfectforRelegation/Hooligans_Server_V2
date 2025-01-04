package com.joh.epl.exception;

import com.joh.epl.exception.kakao.KakaoAuthorizationCodeNullPointerException;
import com.joh.epl.exception.kakao.KakaoGetUserInfoException;
import com.joh.epl.exception.kakao.KakaoUserInfoConvertValueException;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler
  public Mono<ResponseEntity<ErrorResponse>> catchKakaoAuthorizationCodeNullPointerException(
      KakaoAuthorizationCodeNullPointerException e) {

    return Mono.just(
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(e.getMessage()))
    );
  }

  @ExceptionHandler
  public Mono<ResponseEntity<ErrorResponse>> catchKakaoGetUserInfoException(
      KakaoGetUserInfoException e) {

    return Mono.just(
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse(e.getMessage()))
    );
  }

  @ExceptionHandler
  public Mono<ResponseEntity<ErrorResponse>> catchKakaoUserInfoConvertValueException(
      KakaoUserInfoConvertValueException e) {

    return Mono.just(
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse(e.getMessage()))
    );
  }

  @ExceptionHandler
  public Mono<ResponseEntity<ErrorResponse>> catchUserNotFoundException(UserNotFoundException e) {

    return Mono.just(
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(e.getMessage()))
    );
  }

  @ExceptionHandler
  public Mono<ResponseEntity<ErrorResponse>> catchUserRegistrationException(UserRegistrationException e) {

    return Mono.just(
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse(e.getMessage()))
    );
  }

  @ExceptionHandler
  public Mono<ResponseEntity<ErrorResponse>> catchUserEmailUpdateException(UserEmailUpdateException e) {

    return Mono.just(
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(e.getMessage()))
    );
  }

  private ErrorResponse buildErrorResponse(String message) {

    return ErrorResponse.builder()
        .message(message)
        .build();
  }

  @Getter
  @NoArgsConstructor
  private static class ErrorResponse {

    private LocalDateTime timestamp;
    private String message;

    @Builder
    public ErrorResponse(String message) {
      this.timestamp = LocalDateTime.now();
      this.message = message;
    }
  }
}
