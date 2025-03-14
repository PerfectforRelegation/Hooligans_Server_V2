package com.joh.overview.repository;

import com.joh.overview.entity.MarketPrice;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MarketPriceRepository extends ReactiveCrudRepository<MarketPrice, Long> {

  // 금일 오전 9시 가격 조회 (모든 코인의 기준 가격 조회)
  @Query("""
      SELECT mp.* FROM market_prices mp
      INNER JOIN epl_coins ec ON mp.epl_coin_id = ec.id
      WHERE DATE(mp.start_time) = CURRENT_DATE 
      AND TIME(mp.start_time) = '09:00:00'
  """)
  Flux<MarketPrice> findOpeningPricesForToday();
//  SELECT mp.* FROM market_prices mp
//  INNER JOIN epl_coins ec ON mp.epl_coin_id = ec.id
//  WHERE mp.start_time BETWEEN CONCAT(CURRENT_DATE, ' 09:00:00') AND CONCAT(CURRENT_DATE, ' 09:00:00');
}

// TODO: 2025-02-19 아이디, 가격 받고