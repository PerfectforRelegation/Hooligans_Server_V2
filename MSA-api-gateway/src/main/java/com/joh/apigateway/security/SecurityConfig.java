package com.joh.apigateway.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

    return http
        .csrf(CsrfSpec::disable)
        .httpBasic(HttpBasicSpec::disable)
        .formLogin(FormLoginSpec::disable)
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint((exchange, ex) -> {

          // HTTP 상태 401로 응답 작성
          exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
          exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

          // 에러 메시지 생성
          DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
          DataBuffer dataBuffer = bufferFactory.wrap("{\"error\": \"Unauthorized\"}".getBytes());

          return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        }))
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeExchange(authorize -> authorize
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .pathMatchers(HttpMethod.POST, "/core/kakao/users/auth").permitAll()
            .pathMatchers(HttpMethod.GET, "/kakao/auth").permitAll()
            .pathMatchers("/core/**").permitAll()
            .pathMatchers("/coin/**").permitAll()
            .anyExchange().authenticated()
        )
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:5173")); // 클라이언트 도메인 허용
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용 HTTP 메서드
    config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
    config.setAllowCredentials(true); // 쿠키 허용

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 CORS 설정 적용
    return source;
  }
}
