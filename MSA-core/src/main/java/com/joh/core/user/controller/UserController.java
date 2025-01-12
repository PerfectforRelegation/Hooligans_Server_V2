package com.joh.core.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "유저 관련 API 입니당!")
public class UserController {

  @Operation(
      summary = "유저 테스트 API",
      description = "테스트용 API: X-USER-ID(OAuth Id) 헤더 값 반환함!",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "정상 응답",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(example = "{\"oauthId\": \"KAKAO_0000000\"}") // 예시 출력값
              )
          )
      }
  )
  @GetMapping("/users/test")
  public Mono<ResponseEntity<Map<String, String>>> test(
      @Parameter(
          description = "X-USER-ID의 값 확인",
          required = true,
          example = "KAKAO_00000"
      )
      @RequestHeader("X-USER-ID") String oauthId) {

    Map<String, String> res = new HashMap<>();
    res.put("oauthId", oauthId);

    return Mono.just(ResponseEntity.ok(res));
  }
}
