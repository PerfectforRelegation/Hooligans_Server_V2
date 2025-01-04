package com.joh.coin.repository;

import com.joh.coin.entity.UserCoin;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserCoinRepository extends ReactiveCrudRepository<UserCoin, Long> {

}
