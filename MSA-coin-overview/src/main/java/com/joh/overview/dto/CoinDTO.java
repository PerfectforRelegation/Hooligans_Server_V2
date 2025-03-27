package com.joh.overview.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CoinDTO {   // 홈에서 표현할 코인 데이터

  private Long id;
  private String name;        // 코인 이름 (EPL 구단 이름)
  private String symbol;      // 코인 심볼
  private Long initialPrice;  // 초기가
  private Long currentPrice;  // 현재가

  @Builder
  public CoinDTO(Long id, String name, String symbol, Long initialPrice, Long currentPrice) {
    this.id = id;
    this.name = name;
    this.symbol = symbol;
    this.initialPrice = initialPrice;
    this.currentPrice = currentPrice;
  }

  public UpdatedCoinDTO updateCurrentPrice(Long updatedCurrentPrice) {
    this.currentPrice = updatedCurrentPrice;

    return UpdatedCoinDTO.builder()
        .symbol(this.symbol)
        .changedCurrentPrice(this.currentPrice)
        .build();
  }

  // 배치 시 초기가 수정 메서드도 필요함
  public void updateInitialPriceByBatch(Long updatedInitialPrice) {
    this.initialPrice = updatedInitialPrice;
  }
}
