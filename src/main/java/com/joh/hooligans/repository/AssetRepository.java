package com.joh.hooligans.repository;

import com.joh.hooligans.entity.Asset;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AssetRepository extends ReactiveCrudRepository<Asset, Long> {

}
