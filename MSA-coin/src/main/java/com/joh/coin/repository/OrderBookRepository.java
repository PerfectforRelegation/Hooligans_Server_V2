package com.joh.coin.repository;

import com.joh.coin.entity.OrderBook;
import java.time.LocalDateTime;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderBookRepository extends ReactiveCrudRepository<OrderBook, Long> {

  Flux<OrderBook> findByCoinIdAndPriceAndTypeAndStatusOrderByCreatedAtAsc(Long coinId, Long price, String type, String status);

  Mono<OrderBook> findByUserIdAndCoinIdAndPriceAndTypeAndStatusAndCreatedAt(
      String userId, Long coinId, Long price, String type, String status, LocalDateTime createdAt);
}
