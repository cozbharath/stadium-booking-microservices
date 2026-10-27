package com.stadiumbooking.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.stadiumbooking.common.security.CommonSecurity;
import com.stadiumbooking.common.security.GatewayAuthFilter;
import com.stadiumbooking.common.security.InternalTokenFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;
	private final GatewayAuthFilter gatewayAuthFilter;
	private final InternalTokenFilter internalTokenFilter;

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		CommonSecurity.apply(http, gatewayAuthFilter, internalTokenFilter)
				.authenticationProvider(authenticationProvider())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/internal/**").hasRole("INTERNAL")
						.requestMatchers("/api/auth/**").permitAll().requestMatchers(HttpMethod.GET, "/api/users/me")
						.authenticated().requestMatchers(HttpMethod.PUT, "/api/users/me/password").hasAnyRole("ADMIN", "STAFF")
						.requestMatchers("/api/users/**").hasRole("ADMIN").anyRequest().authenticated());

		return http.build();
	}
}