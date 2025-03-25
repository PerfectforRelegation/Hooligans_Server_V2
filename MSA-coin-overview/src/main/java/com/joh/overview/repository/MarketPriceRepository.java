package com.joh.overview.repository;

import com.joh.overview.entity.MarketPrice;
import com.joh.overview.repository.projection.CurrentPriceProjection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MarketPriceRepository extends ReactiveCrudRepository<MarketPrice, Long> {

  // 금일 오전 9시 가격 조회 (모든 코인의 기준 가격 조회)
//  @Query("""
//      SELECT mp.* FROM market_prices mp
//      INNER JOIN epl_coins ec ON mp.epl_coin_id = ec.id
//      WHERE DATE(mp.start_time) = CURRENT_DATE
//      AND TIME(mp.start_time) = '09:00:00'
//  """)
//  Flux<MarketPrice> findOpeningPricesForToday();
//  SELECT mp.* FROM market_prices mp
//  INNER JOIN epl_coins ec ON mp.epl_coin_id = ec.id
//  WHERE mp.start_time BETWEEN CONCAT(CURRENT_DATE, ' 09:00:00') AND CONCAT(CURRENT_DATE, ' 09:00:00');

  // 코인 현재가 가져오는 메서드
  @Query("""
    SELECT current_price
    FROM market_prices
    WHERE coin_id = :coinId 
    ORDER BY created_at DESC 
    LIMIT 1
  """)
  Mono<CurrentPriceProjection> findLatestCurrentPriceByCoinId(Long coinId);
}
