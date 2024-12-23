package com.example.hooligans.config.security;

import com.example.hooligans.config.redis.RedisUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final Key key;
  private final RedisUtil redisUtil;
  private static final long ACCESS_TOKEN_EXPIRATION_TIME = 60 * 1000; // 1분
  private static final long REFRESH_TOKEN_EXPIRATION_TIME = 60 * 1000 * 10; // 10분

  public JwtUtil(@Value("${jwt.secret-key}") String secretKey, RedisUtil redisUtil) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
    this.redisUtil = redisUtil;
  }

  public String createAccessToken(String oauthId) {

    Date nowTime = new Date(System.currentTimeMillis());
    Date expirationTime = new Date(nowTime.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);

    return Jwts.builder()
        .subject(oauthId)   // setSubject() -> subject()로 sub 클레임 설정
        .issuedAt(nowTime)  // 발급 시간 설정
        .expiration(expirationTime)  // 만료 시간 설정
        .signWith(key)  // 서명
        .compact();
  }

  public String createRefreshToken(String oauthId) {

    Date nowTime = new Date(System.currentTimeMillis());
    Date expirationTime = new Date(nowTime.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);

    return Jwts.builder()
        .subject(oauthId)
        .issuedAt(nowTime)
        .expiration(expirationTime)
        .signWith(key)
        .compact();
  }
}
