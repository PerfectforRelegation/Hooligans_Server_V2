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
@Table(name = "epl_coins")
public class EplCoin {

  @Id
  private Long id;

  @Column("name")
  private String name;

  @Column("symbol")
  private String symbol;

  @Column("current_price")
  private BigDecimal currentPrice;

  @Column("total_supply")
  private BigDecimal totalSupply;

  @Column("created_at")
  private LocalDateTime createdAt;
}
