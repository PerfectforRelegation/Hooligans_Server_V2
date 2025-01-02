package com.joh.coin.repository;

import com.joh.coin.entity.OrderBook;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderBookRepository extends ReactiveCrudRepository<OrderBook, Long> {

}
