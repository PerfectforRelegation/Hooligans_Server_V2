package com.example.hooligans.service;

import com.example.hooligans.dto.kakao.KakaoUserInfo;
import com.example.hooligans.dto.kakao.SocialUserResponse;
import com.example.hooligans.entity.User;
import com.example.hooligans.entity.utils.OAuthProvider;
import com.example.hooligans.exception.kakao.KakaoUserInfoConvertValueException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

  @Value("${kakao.client-id}")
  private String clientId;

  @Value("${kakao.uri.auth-token}")
  private String authTokenUri;

  @Value("${kakao.uri.redirect}")
  private String redirectUri;

  private final WebClient webClient;
  private final UserService userService;

  public Mono<Map<String, Object>> getAccessToken(String code) {

    return webClient.post()
        .uri(authTokenUri)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(createAccessTokenRequestBody(code))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<>() {});
  }

  private BodyInserter<?,? super ClientHttpRequest> createAccessTokenRequestBody(String code) {

    return BodyInserters.fromFormData("grant_type", "authorization_code")
        .with("client_id", clientId)
        .with("redirect_uri", redirectUri)
        .with("code", code);
  }

  public Mono<KakaoUserInfo> getUserInfo(String accessToken) {

    return webClient.get()
        .uri("https://kapi.kakao.com/v2/user/me")
        .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<>() {})
        .map(this::parseUserInfo);
  }

  public KakaoUserInfo parseUserInfo(Object userInfo) {
    ObjectMapper mapper = new ObjectMapper();

    try {
      return mapper.convertValue(userInfo, KakaoUserInfo.class);

    } catch (Exception e) {
      throw new KakaoUserInfoConvertValueException("사용자 정보 데이터 변환 중에 오류가 있습니다.");
    }
  }

  public Mono<User> processUserLogin(KakaoUserInfo kakaoUserInfo) {

    Long id = kakaoUserInfo.getId();
    String email = kakaoUserInfo.getKakaoAccount().getEmail();
    String nickname = kakaoUserInfo.getKakaoAccount().getProfile().getNickname();

    String oAuthId = OAuthProvider.KAKAO + "_" + id;

    return userService.isSignUp(oAuthId)
        .flatMap(exist -> {
          if (exist) {
            return userService.getUserByOAuthId(oAuthId);
          } else {

            SocialUserResponse userResponse = SocialUserResponse.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .build();

            return userService.registerNewUser(userResponse);
          }
        });
  }
}
