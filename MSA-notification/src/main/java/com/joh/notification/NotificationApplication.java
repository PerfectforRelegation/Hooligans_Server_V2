package com.joh.notification;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NotificationApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    SpringApplication.run(NotificationApplication.class, args);
  }

}
