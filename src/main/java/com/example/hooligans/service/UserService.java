package com.example.hooligans.service;

import com.example.hooligans.dto.kakao.SocialUserRequest;
import com.example.hooligans.entity.User;
import com.example.hooligans.exception.UserNotFoundException;
import com.example.hooligans.exception.UserRegistrationException;
import com.example.hooligans.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public Mono<Boolean> isSignUp(String oAuthId) {

    return userRepository.existsByOauthId(oAuthId);
  }

  public Mono<User> getUserByOauthId(String oauthId) {

    return userRepository.findByOauthId(oauthId)
        .switchIfEmpty(
            Mono.error(new UserNotFoundException("oAuthId 값에 따른 User가 조회되지 않습니다."))
        );
  }

  public Mono<User> registerUser(SocialUserRequest userResponse) {

    User newUser = userResponse.toEntity();

    return userRepository.save(newUser)
        .onErrorMap(e -> new UserRegistrationException("유저 등록에 실패했습니다. - " + e.getMessage()));
  }
}
