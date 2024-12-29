package com.example.hooligans.repository;

import com.example.hooligans.entity.MarketPrice;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MarketPriceRepository extends ReactiveCrudRepository<MarketPrice, Long> {

}
