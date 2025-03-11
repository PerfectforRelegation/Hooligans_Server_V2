package com.joh.coin.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joh.coin.dto.response.CoinDTO;
import com.joh.coin.dto.response.UpdatedCoinDTO;
import com.joh.coin.service.MarketPriceService;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

// TODO: 2025-03-06
/*
 * <배치 작업>
 * 1시간 마다 현재가 저장
 * */

@Component
@RequiredArgsConstructor
public class CoinWebSocketHandler implements WebSocketHandler {

  private final MarketPriceService marketPriceService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  // 00시 기준 초기 가격
  private final Map<String, Long> initialPrice = new ConcurrentHashMap<>();

  // 현재 코인 데이터
  private final Map<Long, CoinDTO> coins = new ConcurrentHashMap<>();

  // `Sinks.many().multicast()` - 여러 개의 Subscriber가 받을 수 있음
  // 이 코드는 실시간으로 변동하는 코인 가격을 다수의 구독자(클라이언트)에게 전송하기 위한 Sink!
  // `multicast()`를 사용하면 새로운 Subscriber가 생겨도 기존 데이터는 전달되지 않고,
  // 새 데이터부터 수신 가능
  // `.directBestEffort()`: 백프레셔 처리를 최소한으로 하며, 빠르게 데이터 전송을 목표로 함
  private final Sinks.Many<Collection<CoinDTO>> coinsSink = Sinks.many().multicast().onBackpressureBuffer();
  private final Sinks.Many<UpdatedCoinDTO> updatedPriceSink = Sinks.many().multicast().onBackpressureBuffer();

  // TODO: 2025-03-05 변동률도 계산해야 함

  // TODO: 2025-03-06 유저 자산 정보 실시간 변동
  // 이건 유저의 보유 코인과 수량, 사용한 돈을 보내면 될둣...?



  @PostConstruct
  public void initialize() {

  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    System.out.println("-----WebSocket 연결됨----- : " + session.getId());

    // 처음 접속을 했으면, 현재 시세를 보내 주면 끝.
    Mono<Void> initialDataSend = session.send(
        Mono.just(new ArrayList<>(coins.values()))
            .map(this::convertToJson)
            .map(session::textMessage)
    );

    // 이후부터 변동되는 시세를 보여 주면 됨.
    Flux<WebSocketMessage> priceUpdates = updatedPriceSink.asFlux()
        .map(this::convertToJson)
        .map(session::textMessage);

    return initialDataSend.thenMany(priceUpdates).then();
  }

  // 실시간 가격 업데이트
  public Mono<Void> updateCurrentPrice(Long coinId, Long newPrice) {
    CoinDTO coin = coins.get(coinId);
    String symbol = coin.getSymbol();
    Long oldPrice = initialPrice.get(symbol);
    UpdatedCoinDTO updatedCoinDTO = coin.updateCurrentPriceAndChange(oldPrice, newPrice);

    updatedPriceSink.tryEmitNext(updatedCoinDTO);
    return null;
  }

  // JSON 변환
  private <T> String convertToJson(T data) {
    try {
      return objectMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("JSON 변환 오류", e);
    }
  }

//  // 변동률 계산 공식: (현재가 - 오전 9시 가격) / 오전 9시 가격 * 100
//  private BigDecimal calculatePercentageChange(Long basePrice, Long currentPrice) {
//    if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) == 0) {
//      return BigDecimal.ZERO;
//    }
//    return currentPrice.subtract(basePrice)
//        .divide(basePrice, 4, RoundingMode.HALF_UP)
//        .multiply(BigDecimal.valueOf(100));
//  }
}
