package com.joh.common.coin;

import com.joh.common.coin.Coin;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CoinRepository extends ReactiveCrudRepository<Coin, Long> {

}
