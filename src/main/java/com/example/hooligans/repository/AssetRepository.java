package com.example.hooligans.repository;

import com.example.hooligans.entity.Asset;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AssetRepository extends ReactiveCrudRepository<Asset, Long> {

}
