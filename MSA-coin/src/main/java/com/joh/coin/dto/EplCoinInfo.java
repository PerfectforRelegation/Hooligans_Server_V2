package com.joh.coin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class EplCoinInfo {

  private String name;        // 코인 이름 (EPL 구단 이름)
  private String symbol;      // 코인 심볼
  private Long currentPrice;   // 현재 시세
  private Long change;  // 24시간 변동률

  @Builder
  public EplCoinInfo(String name, String symbol, Long currentPrice, Long change) {
    this.name = name;
    this.symbol = symbol;
    this.currentPrice = currentPrice;
    this.change = change;
  }
}
