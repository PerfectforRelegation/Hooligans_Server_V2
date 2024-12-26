package com.example.hooligans.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Table("market_candles")
public class MarketCandle {

  @Id
  @Column("id")
  private Long id;

  @Column("club_id")
  private Long clubId;

  @Column("timeframe")
  private String timeframe; // ENUM('1M', '1D', '1W', '1M', '1Y')

  @Column("start_time")
  private LocalDateTime startTime;

  @Column("end_time")
  private LocalDateTime endTime;

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
