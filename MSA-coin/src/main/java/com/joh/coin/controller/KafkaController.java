package com.joh.coin.controller;

import com.joh.common.kafka.ReactiveKafkaProducer;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaController {

  private final ReactiveKafkaProducer kafkaProducer;

  @PostMapping("/send")
  public Mono<String> sendMessage(@RequestBody Map<String, String> request) {
    String message = request.get("message");
    return kafkaProducer.sendMessage("example-topic", message)
        .thenReturn("✅ Message sent: " + message); // 클라이언트에게 return
  }
}
