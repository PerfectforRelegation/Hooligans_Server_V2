package com.example.hooligans.repository;

import com.example.hooligans.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

  Mono<Boolean> existsByOauthId(String oAuthId);

  Mono<User> findByOauthId(String oAuthId);
}
