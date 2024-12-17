package com.example.hooligans.service;

import com.example.hooligans.dto.kakao.KakaoUserInfo;
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
        .retrieve() // HTTP 응답 처리 수행 메서드
        .bodyToMono(new ParameterizedTypeReference<>() {});
        /*
        * ParameterizedTypeReference?
        * 제네릭 타입을 안전하게 처리
        * 제네릭 타입 정보 유지를 위한 추상클래스로
        * {} 내부에는 익명 클래스 구현체가 들어간다. (일반적으로 비워 둔다고 함)
        * Map<String, Object> 같은 제네릭 타입을 안전하게 역직렬화
        * 런타임 시 위 타입 정보를 유지
        * */
  }

  /*
  * BodyInserter
  * WebClient -> 비동기식 HTTP 요청 처리하기에
  * 요청 본문에 데이터를 삽입해야 할 때 BodyInserter 인터페이스를 사용함
  * 제네릭 구조 -> BodyInserter<T, S>
  * T: 요청 본문 데이터의 타입, S: 삽입 가능한 HTTP 요청 타입
  * 다양한 형식을 지원해 API 요청 작업을 유연하게 함
  * */
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
        /*
        * Spring WebFlux란?
        * Spring 5에서 도입된 리액티브 웹 프레임워크
        * 비동기 프로그래밍 모델을 기반으로 논블로킹 방식의 REST API를 개발할 수 있게 함
        * 아래 예시에서 리액티브 연산자 지원으로 map(), flatMap()을 썼지만
        * filter(), collectList()도 있음
        * 클라이언트 요청 수신 -> WebClient, RouterFunction을 통해 요청 수신
        * 비동기 작업 실행 -> 서비스 레이어는 비동기 방식으로 DB/외부 API와 통신
        * 데이터 반환 -> Mono<T> 또는 Flux<T>로 결과 반환
        *
        * map() -> ※ flatMap()과 비교
        * 동기적 데이터 변환 (비동기 작업 수행 X)
        * 입력 값을 다른 (기본)타입으로 변환할 때 사용
        * DTO 변환, 단일 필드 매핑 등
        *
        * ex 1) DTO 매핑 (데이터 변환)
        * public Mono<UserDTO> getUserAsDTO(String email) {
        *   return userRepository.findByEmail(email)
        *     .map(user -> new UserDTO(user.getEmail(), user.getNickname()));  // 동기적 변환
        * }
        *
        * ex 2) 단순 데이터 변환
        * public Mono<String> getUserNickname(String email) {
        *   return userRepository.findByEmail(email)
        *     .map(User::getNickname);  // 닉네임만 반환
        * }
        *
        * ---
        *
        * flatMap()
        * 비동기 체인 연산자
        * 입력 값을 사용해 비동기 작성 수행이 필요할 때 사용
        * 반환 타입은 항상 리액티브 타입(Mono, Flux)여야 함
        * 내부적으로 비동기 작업 실행 후 스트림을 펼침(flatten)
        * API 호출, DB 작업 등
        *
        * ex 1) 비동기 데이터 조회
        * public Mono<Order> getUserOrder(String email) {
        *   return userRepository.findByEmail(email)
        *     .flatMap(user -> orderRepository.findOrderByUserId(user.getId()));  // 비동기 체인
        * }
        *
        * ex 2) 비동기 데이터 저장 후 확인
        * public Mono<User> createUser(User user) {
        *   return userRepository.save(user)
        *     .flatMap(savedUser -> emailService.sendWelcomeEmail(savedUser)
        *       .then(Mono.just(savedUser)));  // 이메일 발송 후 사용자 반환
        * }
        * */
  }

  public KakaoUserInfo parseUserInfo(Object userInfo) {
    ObjectMapper mapper = new ObjectMapper();

    try {
      return mapper.convertValue(userInfo, KakaoUserInfo.class);

    } catch (Exception e) {
      throw new KakaoUserInfoConvertValueException("사용자 정보 데이터 변환 중에 오류가 있습니다.");
    }
  }
}
