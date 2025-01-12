package com.joh.apigateway.security;

import com.joh.common.security.JwtUtil;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

  private final JwtUtil jwtUtil;
  private final PathMatcher pathMatcher = new AntPathMatcher();

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

    System.out.println("필터 작동!");
    System.out.println("exchange.getRequest().getURI() = " + exchange.getRequest().getURI().getPath());
    System.out.println("exchange.getRequest().getMethod() = " + exchange.getRequest().getMethod());

    String path = exchange.getRequest().getURI().getPath();

    // 경로 패턴 리스트
    Set<String> publicPatterns = Set.of(
        "/kakao/auth",
        "/core/kakao/users/auth",
        "/core/v3/**",
        "/coin/v3/**",
        "/v3/api-docs/**", // OpenAPI 문서 경로
        "/core/webjars/swagger-ui/**", // Core 서비스 Swagger UI
        "/coin/webjars/swagger-ui/**", // Coin 서비스 Swagger UI
        "/webjars/swagger-ui/**" // 공통 Swagger UI 정적 리소스
    );

    // 인증이 필요 없는 경로인 경우 필터를 통과
    if (publicPatterns.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
      return chain.filter(exchange);
    }

    String accessToken = extractAccessTokenFromHeader(exchange);

    if (accessToken == null) {
      return sendErrorResponse(exchange, "Authorization 헤더가 없습니다.");
    }

    // 엑세스 토큰 체크
    return jwtUtil.isAccessTokenExpired(accessToken)
        .flatMap(isExpired -> {
          if (isExpired) {
            // 엑세스 토큰 만료면, 리프레시 토큰 체크
            System.out.println("isExpired = " + isExpired);
            return jwtUtil.validateRefreshToken(accessToken)
                .flatMap(isValid -> {
                  if (isValid) {
                    System.out.println("isValid = " + isValid);
                    return jwtUtil.extractClaims(accessToken)
                        .flatMap(claims -> {
                          String oauthId = claims.getSubject();
                          Authentication auth = new UsernamePasswordAuthenticationToken(oauthId, null, List.of());
                          SecurityContextImpl context = new SecurityContextImpl(auth);

                          return jwtUtil.createAccessToken(oauthId)
                              .flatMap(newAccessToken -> {

                                System.out.println("X-ACCESS-TOKEN 활성화");
                                exchange.getResponse().getHeaders().add("X-ACCESS-TOKEN", newAccessToken);

                                return chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                              });
                        });
                  } else {
                    return sendErrorResponse(exchange, "리프레시 토큰이 유효하지 않습니다. 다시 로그인 해주세요");
                  }
                });
          }

          System.out.println("필터 통과 (엑세스 토큰 유효)");
          exchange.getResponse().getHeaders().remove("X-ACCESS-TOKEN");

          return jwtUtil.extractClaims(accessToken)
              .flatMap(claims -> {
                String oauthId = claims.getSubject();

                // 타 모듈에서 사용자 정보를 간편하게 사용하기 위한 헤더 추가 로직
                ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                        .header("X-USER-ID", oauthId)
                        .build())
                    .build();

                Authentication auth = new UsernamePasswordAuthenticationToken(oauthId, null, List.of());
                SecurityContextImpl context = new SecurityContextImpl(auth);

                return chain.filter(mutatedExchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
              });
        });
  }

  private String extractAccessTokenFromHeader(ServerWebExchange exchange) {

    String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      return authorizationHeader.substring(7); // "Bearer " 이후의 토큰 부분 추출
    }

    System.out.println("authorizationHeader = " + authorizationHeader);
    
    return null;
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
