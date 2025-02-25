package com.joh.coin.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
public class R2dbcConfig {

  // 비동기 방식의 트랜잭션을 관리하는 인터페이스
  // JPA, JDBC 기반의 PlatformTransactionManager 와 달리 WebFlux에서 사용 가능
  // R2dbcTransactionManager는 ReactiveTransactionManager의 구현체이며, R2DBC 데이터베이스와 함께 사용됨
  @Bean
  public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
    return new R2dbcTransactionManager(connectionFactory);
  }

  // 트랜잭션을 적용할 수 있도록 도와주는 도구
  // Mono 또는 Flux 체인에서 .as(trxOperator::transactional)을 사용하면 해당 체인이 트랜잭션 내에서 실행됨
  // 기존 @Transactional 대신 TransactionalOperator를 통해 트랜잭션을 적용할 수 있도록 설정
  @Bean
  public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
    return TransactionalOperator.create(transactionManager); // 트랜잭션을 적용할 수 있는 연산자를 생성
  }
}
