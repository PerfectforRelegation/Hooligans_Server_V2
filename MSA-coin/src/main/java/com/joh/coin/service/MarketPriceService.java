package com.joh.coin.service;

import com.joh.coin.dto.EplCoinInfo;
import com.joh.coin.entity.EplCoin;
import com.joh.coin.entity.MarketPrice;
import com.joh.coin.repository.EplCoinRepository;
import com.joh.coin.repository.MarketPriceRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketPriceService {

  private final MarketPriceRepository marketPriceRepository;
  private final EplCoinRepository eplCoinRepository;

  /*
  * 9시가 되면 시장이 초기화 됨
  * 이 때 웹소켓 핸들러가 메서드를 호출하여 데이터를 가져옴
  * 웹소켓 연결될 때마다 메서드를 호출하면 쿼리도 날라가고 비효율적임
  * 9시가 되면 스케줄링으로 메서드를 한 번 호출하고 맵에다 초기 현재가를 저장함
  * 그리고 처음 호출했던 데이터는 캐싱해 두고 연결될 때마다 캐싱된 데이터를 가져옴
  * 변동이 이뤄지는 현재가는 웹소켓 핸들러에 있는 맵에 계속해서 수정해줌
  * 이러한 변동은 카프카를 통해 데이터들 전달 받음
  * */

  // TODO: 2025-02-06 카프카를 이용해야 함
  // 현재 생각한 로직은
  // 웹소켓 연결 시 캐싱되어 있는 데이터와 현재 시세를 통해
  // 클라이언트에게 계산된 데이터를 보내줌
  // 그리고 현재 시세가 바뀌면 변동률이 함께 바뀌어 계산 및 저장이 되어야 하고
  // 계산된 내용을 보내줘야 함
  // 근데 매번 값이 바뀔 때마다 모든 데이터를 보내주는 것은 비효율적임
  // 즉, 프론트에서도 캐싱이 필요함
  // 새롭게 바뀐 코인 내용만 보내주고,
  // 프론트에서도 그 내용에 대해서만 수정해줘야 함
  // 백엔드에서는 카프카 메시지를 받는 메서드를 두고, 그 안에서 send를 해서 데이터를 보내줄 수 있어야 함

  // 모든 EPL 코인의 최신 시세와 변동률 가져오기 (매일 오전 9시 기준 변동률 초기화)
  public Mono<List<EplCoinInfo>> getInitialMarketPrices() {
    Mono<List<EplCoin>> coinsMono = eplCoinRepository.findAll().collectList()
        .doOnError(e -> System.err.println("❌ EPL 코인 정보 조회 실패: " + e.getMessage()))
        .onErrorResume(e -> {
          System.err.println("⚠️ EPL 코인 정보 조회 실패, 빈 리스트 반환");
          return Mono.just(Collections.<EplCoin>emptyList()); // ⭐ 제네릭 타입 지정
        });

    Mono<List<MarketPrice>> openingPricesMono = marketPriceRepository.findOpeningPricesForToday().collectList()
        .doOnError(e -> System.err.println("❌ 9시 기준 가격 조회 실패: " + e.getMessage()))
        .onErrorResume(e -> {
          System.err.println("⚠️ 9시 기준 가격 조회 실패, 빈 리스트 반환");
          return Mono.just(Collections.<MarketPrice>emptyList()); // ⭐ 제네릭 타입 지정
        });

    return Mono.zip(coinsMono, openingPricesMono)
        .flatMap(tuple -> {
          List<EplCoin> coins = tuple.getT1();
          List<MarketPrice> openingPrices = tuple.getT2();

          // 만약 두 리스트가 모두 비어 있다면 빈 리스트 반환
          if (coins.isEmpty() || openingPrices.isEmpty()) {
            System.err.println("⚠️ 초기 시장 가격이 없습니다.");
            return Mono.just(Collections.<EplCoinInfo>emptyList()); // ⭐ 제네릭 타입 지정
          }

          // 9시 기준 가격 매핑
          Map<Long, BigDecimal> openingPriceMap = openingPrices.stream()
              .collect(Collectors.toMap(MarketPrice::getEplCoinId, MarketPrice::getCurrentPrice, (existing, replacement) -> existing));

          // EplCoinInfo 리스트 생성 (변동률은 0으로 초기화)
          List<EplCoinInfo> coinInfoList = coins.stream()
              .map(coin -> {
                try {
                  return EplCoinInfo.builder()
                      .name(coin.getName())
                      .symbol(coin.getSymbol())
                      .currentPrice(openingPriceMap.getOrDefault(coin.getId(), BigDecimal.ZERO))
                      .change(BigDecimal.ZERO)
                      .build();
                } catch (Exception e) {
                  System.err.println("❌ EplCoinInfo 객체 생성 실패 (코인 ID: " + coin.getId() + "): " + e.getMessage());
                  return EplCoinInfo.builder()
                      .name("Unknown")
                      .symbol("Unknown")
                      .currentPrice(BigDecimal.ZERO)
                      .change(BigDecimal.ZERO)
                      .build();
                }
              })
              .collect(Collectors.toList());

          return Mono.just(coinInfoList);
        })
        .onErrorResume(e -> {
          System.err.println("❌ 전체 초기 시장 가격 조회 실패: " + e.getMessage());
          return Mono.just(Collections.<EplCoinInfo>emptyList()); // ⭐ 제네릭 타입 지정
        });
  }
}
