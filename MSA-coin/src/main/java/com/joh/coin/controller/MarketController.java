package com.joh.coin.controller;

import com.joh.coin.service.MarketService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/market")
public class MarketController {

  private final MarketService marketService;

  @PostMapping("/buy")
  public Mono<?> buyCoin(@RequestBody Map<String, Long> request) {

    return marketService.buyCoin("testId", request);
  }
}
