package com.example.hooligans.repository;

import com.example.hooligans.entity.OrderBook;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderBookRepository extends ReactiveCrudRepository<OrderBook, Long> {

}
