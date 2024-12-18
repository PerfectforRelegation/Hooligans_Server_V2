package com.example.hooligans.repository;

import com.example.hooligans.entity.UserToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserTokenRepository extends ReactiveCrudRepository<UserToken, Long> {

}
