package com.joh.coin.exception;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class CoinExceptionHandler {

  @ExceptionHandler
  public Mono<ResponseEntity<ErrorResponseDTO>> catchFindOrderBookDataException(FindOrderBookDataException e) {
    return Mono.just(
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build())
    );
  }

  @ExceptionHandler
  public Mono<ResponseEntity<ErrorResponseDTO>> catchOrderBookSaveException(OrderBookSaveException e) {
    return Mono.just(
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build())
    );
  }

  @Getter
  @Builder
  private static class ErrorResponseDTO {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;
  }
}
