package com.joh.coin.repository;

import com.joh.coin.entity.Coin;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CoinRepository extends ReactiveCrudRepository<Coin, Long> {

}
