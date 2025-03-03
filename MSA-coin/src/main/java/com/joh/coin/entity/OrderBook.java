package com.joh.coin.entity;

import com.joh.coin.entity.utill.OrderStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

  @Column("coin_id")
  private Long coinId;

  @Column("type")
  private String type; // ENUM 매수냐 매도냐

  @Column("amount")
  private Long amount;

  @Column("price")
  private Long price;

  @Column("status")
  private String status; // 'PENDING', 'COMPLETED', 'CANCELED'

  @Column("created_at")
  private LocalDateTime createdAt;

  @Setter
  @Column("updated_at")
  private LocalDateTime updatedAt;

  public void updateStatusToCompleted() {
    this.status = OrderStatus.COMPLETED.name();
    this.updatedAt = LocalDateTime.now();
  }

  public void updateAmount(long changedAmount) {
    this.amount = changedAmount;
  }

  @Builder
  public OrderBook(String userId, Long coinId, String type, Long amount, Long price, String status,
      LocalDateTime createdAt) {
    this.userId = userId;
    this.coinId = coinId;
    this.type = type;
    this.amount = amount;
    this.price = price;
    this.status = status;
    this.createdAt = createdAt;
  }
}
