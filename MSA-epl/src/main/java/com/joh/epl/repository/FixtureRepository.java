package com.joh.epl.repository;

import com.joh.epl.entity.Fixture;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FixtureRepository extends ReactiveMongoRepository<Fixture, String> {

}
