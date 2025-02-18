package com.joh.common.kafka;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactiveKafkaConsumer {

  private final ReactiveKafkaConsumerTemplate<String, String> kafkaConsumerTemplate;

  @PostConstruct
  public void consumeMessages() {
    kafkaConsumerTemplate.receive()
        .map(record -> {
          System.out.println("Received Message: " + record.value());
          return record;
        })
        .onErrorContinue((ex, record) -> System.err.println("Error processing message: " + ex.getMessage()))
        .subscribe();
  }
}
