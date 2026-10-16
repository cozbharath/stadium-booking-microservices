package com.stadiumbooking.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.razorpay.RazorpayClient;

@Configuration
public class RazorpayConfig {

	@Value("${razorpay.key.id}")
	private String keyId;

	@Value("${razorpay.key.secret}")
	private String keySecret;

	@Bean
	public RazorpayClient razorpayClient() throws Exception {
		System.out.println("USING RAZORPAY KEY ID: " + keyId);
		return new RazorpayClient(keyId, keySecret);
	}
}