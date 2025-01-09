package com.joh.core.user.repository;

import com.joh.core.user.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String>, CustomUserRepository {

  Mono<Boolean> existsByOauthId(String oauthId);

  Mono<User> findByOauthId(String oauthId);

  @Override
  Mono<Long> updateEmailByIdAndNewEmail(String id, String newEmail);
}
