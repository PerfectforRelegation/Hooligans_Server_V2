package com.joh.coin.controller;

import com.joh.coin.dto.TradeOrderReq;
import com.joh.coin.dto.TradeOrderRes;
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
  public Mono<ResponseEntity<TradeOrderRes>> buyCoin(@RequestBody TradeOrderReq tradeOrderReq) {

    return orderBookService.buyCoin("testId", tradeOrderReq)
        .map(result -> ResponseEntity.ok().body(result));
  }

  @PostMapping("/sell")
  public Mono<ResponseEntity<TradeOrderRes>> sellCoin(@RequestBody TradeOrderReq tradeOrderReq) {

    return orderBookService.sellCoin("testID", tradeOrderReq)
        .map(result -> ResponseEntity.ok().body(result));
  }
}
