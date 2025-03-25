package com.joh.overview.config;

import com.joh.overview.handler.CandleStickWebSocketHandler;
import com.joh.overview.handler.CoinWebSocketHandler;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

@Configuration
public class WebsocketConfig {

  @Bean
  public HandlerMapping webSocketMapping(
      CoinWebSocketHandler coinWebSocketHandler,
      CandleStickWebSocketHandler candleStickWebSocketHandler
  ) {
    return new SimpleUrlHandlerMapping(Map.of(
        "/coins", coinWebSocketHandler, // ws://localhost:8080/coin/coins -> 모든 코인 실시간 데이터
        "/coins/{symbol}/candlestick", candleStickWebSocketHandler // 특정
    ), 1);
  }
}
