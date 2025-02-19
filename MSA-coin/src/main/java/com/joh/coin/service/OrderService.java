package com.joh.coin.service;

import com.joh.coin.entity.OrderBook;
import com.joh.coin.repository.OrderBookRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderBookRepository orderBookRepository;

  public Flux<OrderBook> getOrderBook(Map<String, Long> request) {

    Long coinId = request.get("coinId");
    Long price = request.get("price");

    System.out.println("coinId = " + coinId);
    System.out.println("price = " + price);

    Flux<OrderBook> res = orderBookRepository.findByEplCoinIdAndOrderPriceOrderByCreatedAt(coinId, price);

    return res;
  }
}
