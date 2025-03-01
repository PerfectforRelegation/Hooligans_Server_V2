package com.joh.coin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeOrderRes {

  private String message;

  public TradeOrderRes(String message) {
    this.message = message;
  }
}
