package com.joh.coin.service;

import com.joh.coin.entity.OrderBook;
import com.joh.coin.repository.OrderBookRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MarketService {

  private final OrderBookRepository orderBookRepository;

  public Mono<?> buyCoin(String userId , Map<String, Long> request) {
    // 코인 아이디, 가격, 수량
    Long coinId = request.get("coinId");
    Long price = request.get("price");
    AtomicReference<Long> amountToBuy = new AtomicReference<>(request.get("amount"));

    // 1. 요청 개수 =< orderbook.count
    // 2. 요청 개수 > orderbook.count

    return orderBookRepository.findByEplCoinIdAndOrderPriceOrderByCreatedAt(coinId, price)
        .collectList()
        .flatMap(orderBooks -> {

          List<Mono<Void>> processingMonos = new ArrayList<>();

          for (OrderBook order : orderBooks) {
            if (amountToBuy.get() <= 0) {
              break;
            }

            order.changeStatusToCompleted();

            processingMonos.add(orderBookRepository.save(order).then());
            amountToBuy.getAndSet(amountToBuy.get() - 1);
          }

          for (int i = 0; i < amountToBuy.get(); i++) {
            OrderBook newOrder = OrderBook.builder()
                .orderPrice(price)
                .orderType("BUY")
                .status("PENDING")
                .userId(userId)
                .eplCoinId(coinId)
                .createdAt(LocalDateTime.now())
                .build();

            processingMonos.add(orderBookRepository.save(newOrder).then());
          }

          return Flux.merge(processingMonos).then();
        })
        .thenReturn("매수 끝");
  }
}
