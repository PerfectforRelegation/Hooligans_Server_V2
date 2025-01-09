package com.joh.core.reward.repository;

import com.joh.core.reward.model.Reward;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RewardRepository extends ReactiveMongoRepository<Reward, String> {

}
