package com.joh.coin.repository;

import com.joh.coin.entity.EplCoin;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EplCoinRepository extends ReactiveCrudRepository<EplCoin, Long> {

}
