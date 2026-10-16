package com.stadiumbooking.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.stadiumbooking.common.security.CommonSecurity;
import com.stadiumbooking.common.security.GatewayAuthFilter;
import com.stadiumbooking.common.security.InternalTokenFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final GatewayAuthFilter gatewayAuthFilter;
	private final InternalTokenFilter internalTokenFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		CommonSecurity.apply(http, gatewayAuthFilter, internalTokenFilter)
				.authorizeHttpRequests(auth -> auth.requestMatchers("/internal/**").hasRole("INTERNAL")
						.requestMatchers("/api/seat-holds/**").hasRole("CUSTOMER")
						.requestMatchers(HttpMethod.POST, "/api/bookings").hasRole("CUSTOMER")
						.requestMatchers(HttpMethod.GET, "/api/bookings/my", "/api/bookings/my/**").hasRole("CUSTOMER")
						.requestMatchers(HttpMethod.DELETE, "/api/bookings/**").hasRole("CUSTOMER")
						.requestMatchers(HttpMethod.GET, "/api/bookings/all").hasAnyRole("STAFF", "ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/bookings/*").hasAnyRole("STAFF", "ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/bookings/user/**").hasAnyRole("STAFF", "ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/bookings/event/**").hasAnyRole("STAFF", "ADMIN")
						.anyRequest().authenticated());

		return http.build();
	}
}