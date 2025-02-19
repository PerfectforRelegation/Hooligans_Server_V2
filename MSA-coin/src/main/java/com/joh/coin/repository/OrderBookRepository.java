package com.joh.coin.repository;

import com.joh.coin.entity.OrderBook;
import java.math.BigDecimal;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderBookRepository extends ReactiveCrudRepository<OrderBook, Long> {

  Flux<OrderBook> findByEplCoinIdAndOrderPriceOrderByCreatedAt(Long eplCoinId, Long orderPrice);
}
