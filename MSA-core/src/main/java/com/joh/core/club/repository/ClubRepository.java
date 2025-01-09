package com.joh.core.club.repository;

import com.joh.core.club.model.Club;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ClubRepository extends ReactiveMongoRepository<Club, String> {

}
