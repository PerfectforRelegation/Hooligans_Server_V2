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
  @Column("id")
  private Long id;

  @Column("epl_coin_id")
  private Long eplCoinId;

  @Column("timeframe")
  private String timeframe; // ('1M', '1D', '1W', '1M', '1Y')

  @Column("start_time")
  private LocalDateTime startTime;

  @Column("end_time")
  private LocalDateTime endTime;

  @Column("current_price")
  private BigDecimal currentPrice;

  @Column("open_price")
  private BigDecimal openPrice;

  @Column("close_price")
  private BigDecimal closePrice;

  @Column("high_price")
  private BigDecimal highPrice;

  @Column("low_price")
  private BigDecimal lowPrice;

  @Column("trading_volume")
  private BigDecimal tradingVolume;

  @Column("created_at")
  private LocalDateTime createdAt;
}
