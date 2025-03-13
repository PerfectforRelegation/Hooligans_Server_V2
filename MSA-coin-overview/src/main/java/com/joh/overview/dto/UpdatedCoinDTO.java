package com.joh.overview.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatedCoinDTO {
  private String symbol;
  private BigDecimal changeRate;
}
