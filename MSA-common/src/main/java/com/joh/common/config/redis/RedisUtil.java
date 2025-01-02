package com.joh.common.config.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RedisUtil {

  private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
  private static final String LOGIN_PREFIX = "login:";

  public Mono<Boolean> addRefreshToken(String oauthId, String refreshToken, long expirationMillis) {
    String key = LOGIN_PREFIX + oauthId;
    return reactiveRedisTemplate
        .opsForValue()
        .set(key, refreshToken, Duration.ofMillis(expirationMillis));
  }

  public Mono<Boolean> hasKeyRefreshToken(String oauthId) {
    String key = LOGIN_PREFIX + oauthId;
    return reactiveRedisTemplate
        .hasKey(key);
  }

  public Mono<String> getRefreshToken(String oauthId) {
    String key = LOGIN_PREFIX + oauthId;
    return reactiveRedisTemplate
        .opsForValue()
        .get(key)
        .map(Object::toString)
        .switchIfEmpty(Mono.empty());
  }

  public Mono<Boolean> deleteRefreshToken(String oauthId) {
    String key = LOGIN_PREFIX + oauthId;
    return reactiveRedisTemplate
        .delete(key)
        .map(deletedCount -> deletedCount > 0);
  }
}
