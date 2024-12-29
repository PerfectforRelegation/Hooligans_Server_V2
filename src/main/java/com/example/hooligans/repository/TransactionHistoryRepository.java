package com.example.hooligans.repository;

import com.example.hooligans.entity.TransactionHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TransactionHistoryRepository extends ReactiveCrudRepository<TransactionHistory, Long> {

}
