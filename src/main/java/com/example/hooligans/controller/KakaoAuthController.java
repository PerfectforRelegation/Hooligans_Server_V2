package com.example.hooligans.controller;

import com.example.hooligans.dto.UserResponse;
import com.example.hooligans.exception.kakao.KakaoAuthorizationCodeNullPointerException;
import com.example.hooligans.service.KakaoAuthService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/kakao/users")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class KakaoAuthController {

  private final KakaoAuthService kakaoAuthService;

  @PostMapping("/auth")
  public Mono<ResponseEntity<UserResponse>> auth(@RequestBody Map<String, String> payload) {

    String code = payload.get("code");

    if (code == null || code.isEmpty()) {
      throw new KakaoAuthorizationCodeNullPointerException("인가 코드가 필요합니다.");
    }

    return kakaoAuthService.getAccessToken(code)
        .flatMap(tokens -> {
          String accessToken = (String) tokens.get("access_token");

          return kakaoAuthService.getUserInfo(accessToken)
              .flatMap(kakaoUserInfo -> kakaoAuthService.processUserLogin(kakaoUserInfo)
                  .map(loginCheckDTO -> {

                    UserResponse userResponse = loginCheckDTO.getUserResponse();

                    if (loginCheckDTO.isSignUp()) {
                      return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
                    }

                    return new ResponseEntity<>(userResponse, HttpStatus.OK);
                  })
              );
        });

//    return kakaoAuthService.getAccessToken(code)
//        .flatMap(tokens -> {
//          String accessToken = (String) tokens.get("access_token");
//
//          return kakaoAuthService.getUserInfo(accessToken)
//              .handle((userInfo, sink) -> {
//                KakaoUserInfo user = kakaoAuthService.parseUserInfo(userInfo);
//
//                if (user == null || user.getId() == null) {
//                  sink.error(new KakaoGetUserInfoException("사용자 정보 로딩에 실패했습니다."));
//                  return;
//                }
//
//                Long id = user.getId();
//                String nickname = user.getKakaoAccount().getProfile().getNickname();
//                String email = user.getKakaoAccount().getEmail();
//
//                sink.next(new ResponseEntity<>(
//                    KakaoUserResponse.builder()
//                        .id(id)
//                        .nickname(nickname)
//                        .email(email)
//                        .build(),
//                    HttpStatus.OK
//                ));
//              });
//        });
  }
}
