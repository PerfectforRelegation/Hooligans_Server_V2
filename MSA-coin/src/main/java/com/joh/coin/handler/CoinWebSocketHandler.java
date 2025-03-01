package com.joh.coin.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joh.coin.dto.EplCoinInfo;
import com.joh.coin.service.MarketPriceService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CoinWebSocketHandler implements WebSocketHandler {

  private final MarketPriceService marketPriceService;
  private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환기
  private final Map<String, Long> initialPrices = new ConcurrentHashMap<>(); // 9시 기준 초기 가격
  private final Map<String, Long> latestPrices = new ConcurrentHashMap<>(); // 실시간 가격 업데이트

//  @PostConstruct // 서버 시작 시 초기화
//  public void initialize() {
//    marketPriceService.getInitialMarketPrices()
//        .doOnNext(coinList -> {
//          for (EplCoinInfo coin : coinList) {
//            initialPrices.put(coin.getSymbol(), coin.getCurrentPrice()); // 초기 현재가 저장
//            latestPrices.put(coin.getSymbol(), coin.getCurrentPrice());  // 변동 맵에도 초기 값 저장
//          }
//          System.out.println("✅ 초기 가격 저장 완료: " + initialPrices);
//        })
//        .subscribe();
//  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    System.out.println("✅ WebSocket 연결됨: " + session.getId());

    // 1. 웹소켓 연결 시 초기화
    Mono<List<EplCoinInfo>> initialCoinData = marketPriceService.getInitialMarketPrices()
        .flatMap(coinList -> {
          for (EplCoinInfo coin : coinList) {
            initialPrices.put(coin.getSymbol(), coin.getCurrentPrice()); // 초기 현재가 저장
            latestPrices.put(coin.getSymbol(), coin.getCurrentPrice());  // 변동 맵에도 초기 값 저장
          }
          System.out.println("✅ 초기 가격 저장 완료: " + initialPrices);
          return Mono.just(coinList); // ✅ 변경: 데이터를 반환하도록 수정
        });

// 2. 1초마다 변동률을 계산하여 클라이언트로 전송
    Flux<String> coinInfoFlux = Flux.interval(Duration.ofSeconds(5))
        .flatMap(i -> initialCoinData) // ✅ 변경: 초기 데이터가 보장된 이후 실행
        .map(this::generateCoinInfoJson);

    return session.send(coinInfoFlux.map(session::textMessage));
  }

  // 변동률 계산 후 JSON 변환
  private String generateCoinInfoJson(List<EplCoinInfo> coinList) {
    try {
      System.out.println("🔍 현재 저장된 초기 가격: " + initialPrices);
      System.out.println("🔍 현재 저장된 최신 가격: " + latestPrices);

      List<EplCoinInfo> updatedCoinInfos = coinList.stream()
          .map(coin -> {
            // 초기 가격과 실시간 가격을 비교하여 변동률 계산
            Long initialPrice = initialPrices.getOrDefault(coin.getSymbol(), coin.getCurrentPrice());
            Long latestPrice = latestPrices.getOrDefault(coin.getSymbol(), coin.getCurrentPrice());

            System.out.println("🔍 코인: " + coin.getSymbol() + " | 초기가: " + initialPrice + " | 최신가: " + latestPrice);

//            Long change = calculatePercentageChange(initialPrice, latestPrice);

            return EplCoinInfo.builder()
                .name(coin.getName())
                .symbol(coin.getSymbol())
                .currentPrice(latestPrice)
//                .change(change)
                .build();
          })
          .collect(Collectors.toList());

      return objectMapper.writeValueAsString(updatedCoinInfos);
    } catch (JsonProcessingException e) {
      System.err.println("❌ JSON 변환 오류: " + e.getMessage());
      return "[]";
    }
  }

  // 실시간 가격 업데이트
  public void updatePrice(String coinSymbol, Long newPrice) {
    latestPrices.put(coinSymbol, newPrice);
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
