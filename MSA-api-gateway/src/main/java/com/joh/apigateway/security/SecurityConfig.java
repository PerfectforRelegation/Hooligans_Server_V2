package com.joh.apigateway.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
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

    System.out.println("아니 왜 안 되는데;;");

    return http
        .csrf(CsrfSpec::disable)
        .httpBasic(HttpBasicSpec::disable)
        .formLogin(FormLoginSpec::disable)
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint((exchange, ex) -> {

              System.out.println("시발 왜? - " + ex.getCause().getMessage());
              System.out.println("토큰: " + exchange.getRequest().getCookies().getFirst("accessToken"));
              System.out.println("uri: " + exchange.getRequest().getURI());

              return Mono.error(new RuntimeException("으아아아아아아"));
            }))
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeExchange(authorize -> authorize
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .pathMatchers(HttpMethod.POST, "/epl/kakao/users/auth").permitAll()
            .pathMatchers(HttpMethod.GET, "/kakao/auth").permitAll()
            .pathMatchers("/epl/**").permitAll()
            .pathMatchers("/coin/**").permitAll()
            .pathMatchers("/notification/**").permitAll()
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
