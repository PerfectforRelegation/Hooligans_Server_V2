package com.joh.hooligans.repository;

import com.joh.hooligans.entity.MarketPrice;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MarketPriceRepository extends ReactiveCrudRepository<MarketPrice, Long> {

}
