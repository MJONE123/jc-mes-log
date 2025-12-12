package com.jc.log_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling     // @Scheduled 사용을 위해 추가
public class LogApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogApiApplication.class, args);
	}
}
