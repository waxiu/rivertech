package com.example.rivertech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RivertechApplication {

	public static void main(String[] args) {
		SpringApplication.run(RivertechApplication.class, args);
	}

}
