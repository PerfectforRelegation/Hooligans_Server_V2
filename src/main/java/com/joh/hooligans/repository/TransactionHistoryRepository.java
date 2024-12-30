package com.joh.hooligans.repository;

import com.joh.hooligans.entity.TransactionHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TransactionHistoryRepository extends ReactiveCrudRepository<TransactionHistory, Long> {

}
