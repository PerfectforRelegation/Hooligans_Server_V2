package com.joh.core.user.controller;

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
public class UserController {

  @GetMapping("/users/test")
  public Mono<ResponseEntity<Map<String, String>>> test(@RequestHeader("X-USER-ID") String oauthId) {

    Map<String, String> res = new HashMap<>();
    res.put("oauthId", oauthId);

    return Mono.just(ResponseEntity.ok(res));
  }
}
