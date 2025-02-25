package com.joh.coin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeOrderReq {

  private Long coinId;
  private Long price;
  private Long amount;
}
