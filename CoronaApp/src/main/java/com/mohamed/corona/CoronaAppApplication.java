package com.mohamed.corona;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoronaAppApplication {

	public static void main(String[] args) {

		SpringApplication.run(CoronaAppApplication.class, args);
	}

}
