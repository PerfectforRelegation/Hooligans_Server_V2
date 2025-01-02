package com.joh.epl.repository;

import com.joh.hooligans.entity.Fixture;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface FixtureRepository extends ReactiveCrudRepository<Fixture, Long> {

}
