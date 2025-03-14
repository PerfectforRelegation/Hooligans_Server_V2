package com.joh.overview;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(scanBasePackages = {"com.joh.common", "com.joh.overview"})
@EnableR2dbcRepositories(basePackages = {"com.joh.common.coin", "com.joh.overview.repository"})
@EnableDiscoveryClient
public class CoinOverviewApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    SpringApplication.run(CoinOverviewApplication.class, args);
  }
}
