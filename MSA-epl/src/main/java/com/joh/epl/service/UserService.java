package com.joh.epl.service;

import com.joh.hooligans.dto.kakao.SocialUserRequest;
import com.joh.hooligans.entity.User;
import com.joh.hooligans.exception.UserEmailUpdateException;
import com.joh.hooligans.exception.UserNotFoundException;
import com.joh.hooligans.exception.UserRegistrationException;
import com.joh.hooligans.repository.UserRepository;
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

  public Mono<User> getUserByOauthId(String oauthId, String emailFromUserInfo) {

    return userRepository.findByOauthId(oauthId)
        .switchIfEmpty(
            Mono.error(new UserNotFoundException("oAuthId 값에 따른 User가 조회되지 않습니다."))
        )
        // 이메일 변경 여부를 확인하고 그에 따른 분기가 있긴 하지만
        // 이미 존재하는 객체를 반환할 수 있기에 defer()를 사용할 필요는 없음
        // 지연 실행이 필요한 새로운 데이터 생성 작업이 아니기 때문
        // 때문에, DB 업데이트 작업에서 defer()를 사용
        .flatMap(user -> {

          String userEmail = user.getEmail();

          if (userEmail.equals(emailFromUserInfo)) {
            return Mono.just(user);
          }

          Long userId = user.getId();

          return Mono.defer(() -> updateEmail(userId, emailFromUserInfo));
        });
  }

  private Mono<User> updateEmail(Long userId, String emailFromUserInfo) {

    return userRepository.updateEmailById(userId, emailFromUserInfo)
        .flatMap(updatedRow -> {
          if (updatedRow == 0) {
            return Mono.error(new UserEmailUpdateException("유저 이메일 업데이트에 실패했습니다. updatedRow = 0"));
          }

          return userRepository.findById(userId);
        });
  }

  public Mono<User> registerUser(SocialUserRequest userResponse) {

    User newUser = userResponse.toEntity();

    return userRepository.save(newUser)
        .onErrorMap(e -> new UserRegistrationException("유저 등록에 실패했습니다. - " + e.getMessage()));
  }
}
