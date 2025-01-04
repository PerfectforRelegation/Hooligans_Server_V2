package com.joh.epl.repository;

import com.joh.epl.entity.EplClub;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EplClubRepository extends ReactiveMongoRepository<EplClub, String> {

}
