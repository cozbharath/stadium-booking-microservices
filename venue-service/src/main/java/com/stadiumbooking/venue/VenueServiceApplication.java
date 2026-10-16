package com.stadiumbooking.venue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.stadiumbooking.venue", "com.stadiumbooking.common" })
public class VenueServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VenueServiceApplication.class, args);
	}
}
