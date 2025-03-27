package com.joh.overview.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatedCoinDTO {
  private String symbol;
  private Long changedCurrentPrice;
}
