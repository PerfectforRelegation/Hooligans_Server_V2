package com.joh.hooligans.repository;

import com.joh.hooligans.entity.OrderBook;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderBookRepository extends ReactiveCrudRepository<OrderBook, Long> {

}
