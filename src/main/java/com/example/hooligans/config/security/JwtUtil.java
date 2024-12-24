package com.example.hooligans.config.security;

import com.example.hooligans.config.redis.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtUtil {

  private final SecretKey secretKey;
  private final RedisUtil redisUtil;
  private static final long ACCESS_TOKEN_EXPIRATION_TIME = 60 * 1000; // 1분
  private static final long REFRESH_TOKEN_EXPIRATION_TIME = 60 * 1000 * 10; // 10분

  public JwtUtil(@Value("${jwt.secret-key}") String secretKey, RedisUtil redisUtil) {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    this.redisUtil = redisUtil;
  }

  public Mono<String> createAccessToken(String oauthId) {

    return Mono.fromCallable(() -> {
      Date nowTime = new Date(System.currentTimeMillis());
      Date expirationTime = new Date(nowTime.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);

      return Jwts.builder()
          .subject(oauthId) // setSubject -> subject
          .issuedAt(nowTime)  // 발급 시간 설정
          .expiration(expirationTime)  // 만료 시간 설정
          .signWith(secretKey)  // 서명
          .compact();
      
    }).flatMap(accessToken ->
      createAndStoreRefreshToken(oauthId)
          .thenReturn(accessToken) // 엑세스 토큰 반환
    );
  }

  private Mono<Void> createAndStoreRefreshToken(String oauthId) {

    return Mono.fromCallable(() -> {
      Date nowTime = new Date(System.currentTimeMillis());
      Date expirationTime = new Date(nowTime.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);

      return Jwts.builder()
          .subject(oauthId)
          .issuedAt(nowTime)
          .expiration(expirationTime)
          .signWith(secretKey)
          .compact();
    }).flatMap(refreshToken -> 
        redisUtil.addRefreshToken(oauthId, refreshToken, REFRESH_TOKEN_EXPIRATION_TIME)
        ).then(); // Mono<Void> 반환
  }

  public Mono<Claims> extractClaims(String token) {

    return Mono.fromCallable(() -> {
      try {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

      } catch (ExpiredJwtException e) {
        return e.getClaims();
      }
    });
  }

  public Mono<Boolean> isAccessTokenExpired(String accessToken) {

    return extractClaims(accessToken)
        .map(claims -> {
//          System.out.println("토큰 추출: " + claims.getSubject());
          return  claims.getExpiration().before(new Date());
        })
        .onErrorReturn(ExpiredJwtException.class, true);
  }

  public Mono<Boolean> validateRefreshToken(String accessToken) {

    return extractClaims(accessToken)
        .flatMap(claims -> {
          String oauthId = claims.getSubject();
          return redisUtil.getRefreshToken(oauthId).hasElement();
        });
  }
}
