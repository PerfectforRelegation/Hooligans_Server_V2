package com.joh.epl.repository;

import com.joh.epl.entity.EplClub;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EplClubRepository extends ReactiveCrudRepository<EplClub, Long> {

}
