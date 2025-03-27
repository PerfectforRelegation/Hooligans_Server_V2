package com.joh.coin;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.joh.common", "com.joh.coin"})
@EnableR2dbcRepositories(basePackages = {"com.joh.common.coin", "com.joh.coin.repository"})
public class CoinApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(CoinApplication.class, args);
	}
}
