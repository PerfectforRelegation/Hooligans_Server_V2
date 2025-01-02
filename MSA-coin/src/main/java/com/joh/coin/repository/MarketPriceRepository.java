package com.joh.coin.repository;

import com.joh.coin.entity.MarketPrice;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MarketPriceRepository extends ReactiveCrudRepository<MarketPrice, Long> {

}
