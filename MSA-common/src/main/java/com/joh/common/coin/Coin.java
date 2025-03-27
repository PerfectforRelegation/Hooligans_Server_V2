package com.joh.common.coin;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@NoArgsConstructor
@Table(name = "coins")
public class Coin {

  @Id
  private Long id;

  @Column("name")
  private String name;

  @Column("symbol")
  private String symbol;

  @Column("total_supply")
  private Long totalSupply;

  @Column("created_at")
  private LocalDateTime createdAt;
}
