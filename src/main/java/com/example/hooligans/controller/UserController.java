package com.example.hooligans.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  @GetMapping
  public Mono<ResponseEntity<Map<String, String>>> test(@AuthenticationPrincipal String oauthId) {

    Map<String, String> res = new HashMap<>();
    res.put("oauthId", oauthId);

    return Mono.just(ResponseEntity.ok(res));
  }
}
