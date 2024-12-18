package com.example.hooligans.exception;

import com.example.hooligans.exception.kakao.KakaoAuthorizationCodeNullPointerException;
import com.example.hooligans.exception.kakao.KakaoGetUserInfoException;
import com.example.hooligans.exception.kakao.KakaoUserInfoConvertValueException;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

public class GlobalExceptionHandler {

  @ExceptionHandler
  public Mono<ResponseEntity<ErrorResponse>> catchKakaoAuthorizationCodeNullPointerException(KakaoAuthorizationCodeNullPointerException e) {

    ErrorResponse errorResponse = ErrorResponse.builder()
        .message(e.getMessage())
        .build();

    return Mono.just(
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    );
  }

  @ExceptionHandler
  public Mono<ResponseEntity<ErrorResponse>> catchKakaoGetUserInfoException(KakaoGetUserInfoException e) {

    ErrorResponse errorResponse = ErrorResponse.builder()
        .message(e.getMessage())
        .build();

    return Mono.just(
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    );
  }

  @ExceptionHandler
  public Mono<ResponseEntity<ErrorResponse>> catchKakaoUserInfoConvertValueException(KakaoUserInfoConvertValueException e) {

    ErrorResponse errorResponse = ErrorResponse.builder()
        .message(e.getMessage())
        .build();

    return Mono.just(
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    );
  }

  @ExceptionHandler
  public Mono<ResponseEntity<ErrorResponse>> catchUserNotFoundException(UserNotFoundException e) {

    ErrorResponse errorResponse = ErrorResponse.builder()
        .message(e.getMessage())
        .build();

    return Mono.just(
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    );
  }

  @ExceptionHandler
  public Mono<ResponseEntity<ErrorResponse>> catchUserRegistrationException(UserRegistrationException e) {

    ErrorResponse errorResponse = ErrorResponse.builder()
        .message(e.getMessage())
        .build();

    return Mono.just(
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    );
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
