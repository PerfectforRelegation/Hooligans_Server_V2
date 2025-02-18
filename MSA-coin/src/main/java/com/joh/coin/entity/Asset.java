package com.joh.coin.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@NoArgsConstructor
@Table(name = "assets")
public class Asset {

  @Id
  private Long id;

  @Column("user_id")
  private String userId;

  @Column("asset_type")
  private String assetType;

  @Column("balance")
  private BigDecimal balance;

  @Column("locked_balance")
  private BigDecimal lockedBalance;

  @Column("last_updated")
  private LocalDateTime lastUpdated;
}
