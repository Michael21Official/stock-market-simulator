package com.remitly.stockmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableRetry
@EnableAsync
public class StockMarketSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockMarketSimulatorApplication.class, args);
	}
}