package com.example.hooligans.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@NoArgsConstructor
@Table(name = "order_book")
public class OrderBook {

  @Id
  private Long id;

  @Column("user_id")
  private Long userId;

  @Column("epl_coin_id")
  private Long eplCoinId;

  @Column("order_type")
  private String orderType; // ENUM ('LIMIT', 'MARKET')

  @Column("coin_amount")
  private BigDecimal coinAmount;

  @Column("order_price")
  private BigDecimal orderPrice;

  @Column("status")
  private String status; // 'PENDING', 'COMPLETED', 'CANCELED'

  @Column("expiration_date")
  private LocalDateTime expirationDate;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;
}
