package com.example.hooligans.config.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

  private final RedisTemplate<String, Object> redisTemplate;
  private static final String LOGIN_PREFIX = "login_";

  public void addRefreshToken(String oauthId, String refreshToken, long expiration) {
    redisTemplate.opsForValue().set(LOGIN_PREFIX + oauthId, refreshToken, expiration, TimeUnit.MILLISECONDS);
  }

  public boolean hasKeyRefreshToken(String oauthId) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(LOGIN_PREFIX + oauthId));
  }

  public Object getRefreshToken(String oauthId) {
    return redisTemplate.opsForValue().get(LOGIN_PREFIX + oauthId);
  }

  public void deleteRefreshToken(String oauthId) {
    redisTemplate.delete(LOGIN_PREFIX + oauthId);
  }
}
