package com.joh.epl.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UserController {

  @GetMapping("/users/test")
  public Mono<ResponseEntity<Map<String, String>>> test() {

    Map<String, String> res = new HashMap<>();
    res.put("oauthId", "adada");

    return Mono.just(ResponseEntity.ok(res));
  }
}
