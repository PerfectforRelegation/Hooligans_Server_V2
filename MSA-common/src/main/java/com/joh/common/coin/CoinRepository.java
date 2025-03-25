package com.joh.common.coin;

import com.joh.common.coin.projection.IdAndNameAndSymbolProjection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CoinRepository extends ReactiveCrudRepository<Coin, Long> {

  @Query("SELECT id, name, symbol FROM coins")
  Flux<IdAndNameAndSymbolProjection> findCoinsSummaryProjection();
}
