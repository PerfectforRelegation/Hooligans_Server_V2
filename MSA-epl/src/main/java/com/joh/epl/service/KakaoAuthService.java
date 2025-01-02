package com.joh.epl.service;

import com.joh.hooligans.config.security.JwtUtil;
import com.joh.hooligans.dto.LoginCheckDTO;
import com.joh.hooligans.dto.kakao.KakaoUserInfo;
import com.joh.hooligans.dto.kakao.SocialUserRequest;
import com.joh.hooligans.entity.User;
import com.joh.hooligans.entity.utils.OAuthProvider;
import com.joh.hooligans.exception.kakao.KakaoUserInfoConvertValueException;
import com.joh.hooligans.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
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
  private final UserMapper userMapper;
  private final JwtUtil jwtUtil;

  public Mono<Map<String, Object>> getAccessToken(String code) {

    return webClient.post()
        .uri(authTokenUri)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(createAccessTokenRequestBody(code))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<>() {});
  }

  // fromFormData()가 MultiValueMap<String, String> 타입으로 데이터를 구성함
  // ClientHttpRequest: Spring WebFlux 에서 지원하는 HTTP 요청 대상
  private BodyInserter<MultiValueMap<String, String>, ClientHttpRequest> createAccessTokenRequestBody(String code) {

//    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//    formData.add("grant_type", "authorization_code");
//    formData.add("client_id", clientId);
//    formData.add("redirect_uri", redirectUri);
//    formData.add("code", code);
//
//    return BodyInserters.fromFormData(formData);

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

  public Mono<LoginCheckDTO> processUserLogin(ServerWebExchange exchange, KakaoUserInfo kakaoUserInfo) {

    Long kakaoId = kakaoUserInfo.getId();
    String email = kakaoUserInfo.getKakaoAccount().getEmail();
    String nickname = kakaoUserInfo.getKakaoAccount().getProfile().getNickname();

    String oauthId = OAuthProvider.KAKAO + "_" + kakaoId;

    // Mono.just(): 즉시 데이터 생성 -> 호출 시점에 즉시 실행 및 지연 실행 불가능, 정적 데이터 반환 용도
    // Mono.defer(): 미리 생성된 고정된 값 사용(just)가 아닌, 매번 새로운 데이터 생성
    // 동적 데이터 생성, (비동기 작업, DB 호출 등)
    // ----> 'just()'는 즉시 평가하기 때문에 모든 분기 로직이 실행되며 비효율적일 수 있음
    //
    // public Mono<String> process(int value) {
    //    return (value > 10) ? Mono.just(expensiveOperation())
    //                        : Mono.just("값이 작습니다.");
    // }
    //
    // private String expensiveOperation() {
    //    System.out.println("비싼 작업 실행!");
    //    return "완료!";
    // }
    // --> 비싼 작업 실행!  // 조건과 상관없이 무조건 실행됨
    //     값이 작습니다.

    // public Mono<String> process(int value) {
    //    return (value > 10) ? Mono.defer(() -> Mono.just(expensiveOperation()))
    //                        : Mono.just("값이 작습니다.");
    // }
    //
    // private String expensiveOperation() {
    //    System.out.println("비싼 작업 실행!");
    //    return "완료!";
    // }
    // value = 5 -> 값이 작습니다. (비싼 작업 실행 x), 15 -> 비싼 작업 실행! 완료!

    return userService.isSignUp(oauthId)
        .flatMap(exist -> Mono.defer(() ->
            exist ? loginUser(exchange, oauthId, email) : registerUser(exchange, buildSocialUserResponse(kakaoId, email, nickname))));
  }

  // TODO: 2024-12-19 스웨거 적용 (유저엔 미적용)
  private Mono<LoginCheckDTO> loginUser(ServerWebExchange exchange, String oauthId, String email) {

    return userService.getUserByOauthId(oauthId, email)
        .flatMap(user -> processLogin(exchange, user, false));
  }

  private Mono<LoginCheckDTO> registerUser(ServerWebExchange exchange, SocialUserRequest userRequest) {

    return userService.registerUser(userRequest)
        .flatMap(user -> processLogin(exchange, user, true));
  }

  private SocialUserRequest buildSocialUserResponse(Long socialId, String email, String nickname) {

    return SocialUserRequest.builder()
        .socialId(socialId)
        .email(email)
        .nickname(nickname)
        .build();
  }

  private LoginCheckDTO createLoginResponse(boolean isSignUp, User user) {

    return LoginCheckDTO.builder()
        .isSignUp(isSignUp)
        .userResponse(userMapper.toUserResponse(user))
        .build();
  }

  private Mono<LoginCheckDTO> processLogin(ServerWebExchange exchange, User user, boolean isSignUp) {

    return jwtUtil.createAccessToken(user.getOauthId())
        .flatMap(accessToken -> {
          setAccessTokenCookie(exchange, accessToken);
          return Mono.just(createLoginResponse(isSignUp, user));
        });
  }

  private void setAccessTokenCookie(ServerWebExchange exchange, String accessToken) {
    ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
        .httpOnly(true) // 클라이언트에서 JavaScript로 접근 불가
        .secure(true)   // HTTPS 전용
        .path("/")      // 모든 경로에서 유효
        .maxAge(60 * 60) // 1시간
        .build();

    exchange.getResponse().addCookie(cookie);
  }
}
