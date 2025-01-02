package com.joh.epl.repository;

import com.joh.hooligans.entity.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

  Mono<Boolean> existsByOauthId(String oAuthId);

  Mono<User> findByOauthId(String oAuthId);

  @Modifying
  @Query("UPDATE users SET email = :newEmail WHERE id = :id")
  Mono<Integer> updateEmailById(Long id, String newEmail);
  // 왜 Mono<User>로 하지 않았나?
  // @Modifying -> insert, update, delete 를 실행할 때 필요
  // 기본 동작: Mono<Integer> 영향받은 행 수를 반환 -> Mono<User> 반환 지원 X
}
