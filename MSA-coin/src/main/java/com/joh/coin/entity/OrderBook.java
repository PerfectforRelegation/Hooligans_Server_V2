package com.joh.coin.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
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
  private String userId;

  @Column("epl_coin_id")
  private Long eplCoinId;

  @Column("order_type")
  private String orderType; // ENUM 매수냐 매도냐

  @Column("order_price")
  private Long orderPrice;

  @Column("status")
  private String status; // 'PENDING', 'COMPLETED', 'CANCELED'

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  public void changeStatusToCompleted() {
    this.status = "COMPLETED";
  }

  @Builder
  public OrderBook(String userId, Long eplCoinId, String orderType, Long orderPrice, String status,
      LocalDateTime createdAt) {
    this.userId = userId;
    this.eplCoinId = eplCoinId;
    this.orderType = orderType;
    this.orderPrice = orderPrice;
    this.status = status;
    this.createdAt = createdAt;
  }
}
