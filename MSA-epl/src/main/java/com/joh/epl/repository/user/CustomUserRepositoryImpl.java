package com.joh.epl.repository.user;

import com.joh.epl.model.User;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

  private final ReactiveMongoTemplate mongoTemplate;

  @Override
  public Mono<Long> updateEmailByIdAndNewEmail(String id, String newEmail) {
    Query query = new Query(Criteria.where("_id").is(id));
    Update update = new Update().set("email", newEmail);

    return mongoTemplate.updateFirst(query, update, User.class)
        .map(UpdateResult::getModifiedCount);
  }
}
