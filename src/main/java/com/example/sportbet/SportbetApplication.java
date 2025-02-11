package com.example.sportbet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SportbetApplication {

	public static void main(String[] args) {
		SpringApplication.run(SportbetApplication.class, args);
	}

}
