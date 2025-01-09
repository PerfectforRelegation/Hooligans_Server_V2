package com.joh.core.club.repository;

import com.joh.core.club.model.EplClub;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EplClubRepository extends ReactiveMongoRepository<EplClub, String> {

}
