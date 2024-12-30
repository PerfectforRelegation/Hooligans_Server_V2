package com.joh.hooligans.config.security;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    
//    System.out.println("필터 작동!");
//    System.out.println("exchange.getRequest().getURI() = " + exchange.getRequest().getURI().getPath());
//    System.out.println("exchange.getRequest().getMethod() = " + exchange.getRequest().getMethod());

    String path = exchange.getRequest().getURI().getPath();

    if (path.equals("/kakao/auth") || path.equals("/kakao/users/auth")) {
      return chain.filter(exchange);
    }

    String accessToken = extractAccessTokenFromCookie(exchange);

    if (accessToken == null) return sendErrorResponse(exchange, "엑세스 토큰 값이 없습니다.");

    // 엑세스 토큰 체크
    return jwtUtil.isAccessTokenExpired(accessToken)
        .flatMap(isExpired -> {
          if (isExpired) {
            // 엑세스 토큰 만료면, 리프레시 토큰 체크
//            System.out.println("isExpired = " + isExpired);
            return jwtUtil.validateRefreshToken(accessToken)
                .flatMap(isValid -> {
                  if (isValid) {
//                    System.out.println("isValid = " + isValid);
                    return jwtUtil.extractClaims(accessToken)
                        .flatMap(claims -> {
                          String oauthId = claims.getSubject();
                          Authentication auth = new UsernamePasswordAuthenticationToken(oauthId, null, List.of());
                          SecurityContextImpl context = new SecurityContextImpl(auth);

                          return jwtUtil.createAccessToken(oauthId)
                              .flatMap(newAccessToken -> {
                                exchange.getResponse().addCookie(ResponseCookie.from("accessToken", newAccessToken)
                                        .httpOnly(true)
                                        .path("/")
                                        .maxAge(60 * 60)
                                    .build());

                                return chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                              });
                        });
                  } else {
                    return sendErrorResponse(exchange, "리프레시 토큰이 유효하지 않습니다. 다시 로그인 해주세요");
                  }
                });
          }

//          System.out.println("필터 통과");
          return jwtUtil.extractClaims(accessToken)
              .flatMap(claims -> {
                String oauthId = claims.getSubject();
                Authentication auth = new UsernamePasswordAuthenticationToken(oauthId, null, List.of());
                SecurityContextImpl context = new SecurityContextImpl(auth);

                return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
              });
        });
  }

  private String extractAccessTokenFromCookie(ServerWebExchange exchange) {

    HttpCookie cookie = exchange.getRequest().getCookies().getFirst("accessToken");

//    System.out.println("cookie = " + cookie);
    
    if (cookie == null) return null;

    return cookie.getValue();
  }

  private Mono<Void> sendErrorResponse(ServerWebExchange exchange, String message) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.UNAUTHORIZED);

    // 에러 메시지를 JSON으로 반환 (응답 헤더 설정)
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    DataBuffer buffer = response.bufferFactory()
        .wrap(("{\"error\":\"" + message + "\"}") // 주어진 바이트 배열을 감싸서 새로운 DataBuffer 생성
            .getBytes(StandardCharsets.UTF_8));

    return response.writeWith(Mono.just(buffer)); // 응답 본문에 DataBuffer 쓰기
  }
}
