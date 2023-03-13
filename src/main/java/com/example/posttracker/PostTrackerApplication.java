package com.example.posttracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class PostTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostTrackerApplication.class, args);
	}

}
