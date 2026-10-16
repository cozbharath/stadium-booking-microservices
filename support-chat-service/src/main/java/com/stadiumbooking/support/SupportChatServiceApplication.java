package com.stadiumbooking.support;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = { "com.stadiumbooking.support", "com.stadiumbooking.common" })
@EnableFeignClients
public class SupportChatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupportChatServiceApplication.class, args);
	}
}