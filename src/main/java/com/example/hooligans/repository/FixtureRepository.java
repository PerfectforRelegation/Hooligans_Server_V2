package com.example.hooligans.repository;

import com.example.hooligans.entity.Fixture;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface FixtureRepository extends ReactiveCrudRepository<Fixture, Long> {

}
