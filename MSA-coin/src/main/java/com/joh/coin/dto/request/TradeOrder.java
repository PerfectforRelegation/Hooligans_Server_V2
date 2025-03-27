package com.joh.coin.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeOrder {

  private Long coinId;
  private Long price;
  private Long amount;
}
