package com.leonardof108.TransactionStatsAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TransactionStatsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionStatsApiApplication.class, args);
	}

}
