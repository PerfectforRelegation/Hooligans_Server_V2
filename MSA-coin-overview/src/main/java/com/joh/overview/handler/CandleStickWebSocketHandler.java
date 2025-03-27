package com.joh.overview.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

/**
 * <캔들 스틱 데이터를 다루는 핸들러>
 * 1. 스케쥴러를 통해 데이터를 저장하도록 하자.
 * 2. 1시간 주기로 데이터를 저장하자.
 * 3. 클라이언트가 데이터를 요청하면,
 *    MySQL 에서 가져온 데이터 + 실시간 데이터
 *    이렇게 데이터를 보낼 수 있도록 설계하자.
 * 4. uri 파라미터로 코인의 심볼을 받자.
 *    밭은 심볼에 따라 제공하는 데이터를 나누자.
 *    `Sinks`를 맵에 넣어 다루는 게 나을까...?
* */
@Component
@RequiredArgsConstructor
public class CandleStickWebSocketHandler implements WebSocketHandler {

  @PostConstruct
  public void initialize() {

  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    return null;
  }
}
