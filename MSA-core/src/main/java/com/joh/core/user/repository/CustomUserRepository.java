package com.joh.core.user.repository;

import reactor.core.publisher.Mono;

public interface CustomUserRepository {
  Mono<Long> updateEmailByIdAndNewEmail(String id, String newEmail);
}
