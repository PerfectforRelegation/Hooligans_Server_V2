package com.joh.coin.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Table("market_prices")
public class MarketPrice {

  @Id
  private Long id;

  @Column("coin_id")
  private Long coinId;

  @Column("timeframe") // 1시간 마다
  private String timeframe; // ('1D', '1W', '1M', '1Y')

  @Column("start_time")
  private LocalDateTime startTime;

  @Column("current_price")
  private Long currentPrice;

  @Column("open_price")
  private Long openPrice;

  @Column("close_price")
  private Long closePrice;

  @Column("high_price")
  private Long highPrice;

  @Column("low_price")
  private Long lowPrice;

  @Column("trading_volume")
  private BigDecimal tradingVolume;

  @Column("created_at")
  private LocalDateTime createdAt;
}
