package com.joh.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RedisUtil {

  private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

  public <T> Mono<Void> saveObject(String key, T data) {
    return reactiveRedisTemplate.opsForList()
        .rightPush(key, data)
        .then();
  }

  public <T> Flux<T> getObjectList(String key, Class<T> clazz) {
    return reactiveRedisTemplate.opsForList()
        .range(key, 0, -1) // 전체 리스트 조회
        .cast(clazz);                // 타입 변환
  }
}
