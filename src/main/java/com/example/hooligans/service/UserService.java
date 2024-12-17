package com.example.hooligans.service;

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
}
