package com.example.hooligans.service;

import com.example.hooligans.entity.User;
import com.example.hooligans.exception.UserNotFoundException;
import com.example.hooligans.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public Mono<Boolean> isSignUp(String oAuthId) {

    return userRepository.existsByOAuthId(oAuthId);
  }

  public Mono<User> getUserByOAuthId(String oAuthId) {

    return userRepository.findByOAuthId(oAuthId)
        .switchIfEmpty(
            Mono.error(new UserNotFoundException("oAuthId 값에 따른 User가 조회되지 않습니다."))
        );
  }
}
