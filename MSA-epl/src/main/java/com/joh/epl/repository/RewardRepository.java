package com.joh.epl.repository;

import com.joh.epl.entity.Reward;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RewardRepository extends ReactiveMongoRepository<Reward, String> {

}
