package com.joh.coin.controller;

import com.joh.coin.dto.request.TradeOrder;
import com.joh.coin.dto.response.Message;
import com.joh.coin.service.OrderBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

  private final OrderBookService orderBookService;

  @PostMapping("/buy")
  public Mono<ResponseEntity<Message>> buyCoin(@RequestBody TradeOrder tradeOrder) {

    return orderBookService.buyCoin("testId", tradeOrder)
        .map(result -> ResponseEntity.ok().body(result));
  }

  @PostMapping("/sell")
  public Mono<ResponseEntity<Message>> sellCoin(@RequestBody TradeOrder tradeOrder) {

    return orderBookService.sellCoin("testID", tradeOrder)
        .map(result -> ResponseEntity.ok().body(result));
  }
}
