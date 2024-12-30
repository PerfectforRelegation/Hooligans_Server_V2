package com.joh.hooligans.repository;

import com.joh.hooligans.entity.UserCoin;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserCoinRepository extends ReactiveCrudRepository<UserCoin, Long> {

}
