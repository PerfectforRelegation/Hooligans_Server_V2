package com.joh.hooligans.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Table("transaction_history")
public class TransactionHistory {

  @Id
  @Column("id")
  private Long id;

  @Column("order_book_id")
  private Long orderBookId;

  @Column("user_id")
  private Long userId;

  @Column("epl_coin_id")
  private Long eplCoinId;

  @Column("transaction_type")
  private String transactionType; // ENUM('BUY', 'SELL', 'CANCELED')

  @Column("coin_amount")
  private BigDecimal coinAmount;

  @Column("price")
  private BigDecimal price;

  @Column("total_value")
  private BigDecimal totalValue;

  @Column("transaction_fee")
  private BigDecimal transactionFee;

  @Column("transaction_date")
  private LocalDateTime transactionDate;
}
