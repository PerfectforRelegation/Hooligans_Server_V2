package com.joh.overview.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CoinDTO {

  private String name;        // 코인 이름 (EPL 구단 이름)
  private String symbol;      // 코인 심볼
  private Long currentPrice;   // 현재 시세
  private BigDecimal changeRate;  // 24시간 변동률

  @Builder
  public CoinDTO(String name, String symbol, Long currentPrice, BigDecimal changeRate) {
    this.name = name;
    this.symbol = symbol;
    this.currentPrice = currentPrice;
    this.changeRate = changeRate;
  }

  public UpdatedCoinDTO updateCurrentPriceAndChange(Long initialPrice, Long currentPrice) {
    this.currentPrice = currentPrice;

    BigDecimal updatedChangeRate = calculateChangeRate(initialPrice, currentPrice);
    this.changeRate = updatedChangeRate;
    return UpdatedCoinDTO.builder()
        .symbol(this.symbol)
        .changeRate(updatedChangeRate)
        .build();
  }

  private static BigDecimal calculateChangeRate(Long initialPrice, Long currentPrice) {
    if (initialPrice == null || initialPrice == 0) return BigDecimal.valueOf(0.0);

    BigDecimal newPrice = BigDecimal.valueOf(currentPrice);
    BigDecimal oldPrice = BigDecimal.valueOf(initialPrice);
    return newPrice.subtract(oldPrice).multiply(BigDecimal.valueOf(100));
  }
}
