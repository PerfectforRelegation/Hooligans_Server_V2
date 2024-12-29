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
@Table(name = "user_coins")
public class UserCoin {

  @Id
  private Long id;

  @Column("user_id")
  private Long userId;

  @Column("epl_coin_id")
  private Long eplCoinId;

  @Column("coin_amount")
  private BigDecimal coinAmount;

  @Column("average_purchase_price")
  private BigDecimal averagePurchasePrice;

  @Column("last_updated")
  private LocalDateTime lastUpdated;
}
