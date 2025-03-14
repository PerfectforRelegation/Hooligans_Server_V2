package com.joh.overview.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

  @GetMapping("/test")
  public Mono<ResponseEntity<?>> getTest() {

    Map<String, String> map = new HashMap<>();
    map.put("message", "테스트 api입니다.");

    return Mono.just(ResponseEntity.ok().body(map));
  }
}
