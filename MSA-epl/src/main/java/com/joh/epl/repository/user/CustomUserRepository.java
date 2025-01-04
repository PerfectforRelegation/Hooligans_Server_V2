package com.joh.epl.repository.user;

import reactor.core.publisher.Mono;

public interface CustomUserRepository {
  Mono<Long> updateEmailByIdAndNewEmail(String id, String newEmail);
}
