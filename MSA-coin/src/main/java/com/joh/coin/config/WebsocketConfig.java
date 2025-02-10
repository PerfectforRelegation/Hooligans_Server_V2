package com.joh.coin.config;

import com.joh.coin.handler.CoinWebSocketHandler;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

@Configuration
public class WebsocketConfig {

  @Bean
  public HandlerMapping webSocketMapping(CoinWebSocketHandler coinWebSocketHandler) {
    return new SimpleUrlHandlerMapping(Map.of(
        "/coins", coinWebSocketHandler // ws://localhost:8080/coin/coins
    ), 1);
  }
}
