package com.example.springsse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringsseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringsseApplication.class, args);
	}

}
