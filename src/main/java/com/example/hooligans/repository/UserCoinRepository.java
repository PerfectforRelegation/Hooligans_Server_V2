package com.example.hooligans.repository;

import com.example.hooligans.entity.UserCoin;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserCoinRepository extends ReactiveCrudRepository<UserCoin, Long> {

}
