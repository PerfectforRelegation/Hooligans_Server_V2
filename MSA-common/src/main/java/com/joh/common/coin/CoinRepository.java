package com.joh.common.coin;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CoinRepository extends ReactiveCrudRepository<Coin, Long> {

}
