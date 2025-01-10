package com.joh.core.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@Tag(name = "Notification API", description = "알림 관련 API 입니당!")
public class NotificationController {

  @GetMapping("/test")
  @Operation(
      summary = "테스트 API", // API 제목
      description = "그냥 출력값 잘 나오나 테스트 하는 API입니당ㅋㅋ", // API 설명
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "정상 응답",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(example = "노티피케이션 테스트") // 예시 출력값
              )
          )
      }
  )
  public String test() {
    return "노티피케이션 테스트";
  }
}
