package com.joh.common.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReactiveKafkaProducer {

  private final ReactiveKafkaProducerTemplate<String, String> kafkaProducerTemplate;

  public Mono<Void> sendMessage(String topic, String message) {
    return kafkaProducerTemplate.send(topic, message)
        .doOnSuccess(result -> System.out.println("Message Sent: " + message))
        .doOnError(error -> System.err.println("Error Sending Message: " + error.getMessage()))
        .then();
  }
}
