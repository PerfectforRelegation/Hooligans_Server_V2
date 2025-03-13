package com.joh.common.coin.asset;

import com.joh.coin.entity.Asset;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AssetRepository extends ReactiveCrudRepository<Asset, Long> {

}
