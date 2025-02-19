package com.joh.coin.controller;

import com.joh.coin.entity.OrderBook;
import com.joh.coin.service.OrderService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

  private final OrderService orderService;

  @GetMapping("/fetch")
  public Flux<OrderBook> buyCoin(
//      @RequestHeader("X-USER-ID") String oauthId,
      @RequestBody Map<String, Long> request) {

    return orderService.getOrderBook(request);
  }
}
