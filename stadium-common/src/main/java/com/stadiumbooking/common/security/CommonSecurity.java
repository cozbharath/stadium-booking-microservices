package com.stadiumbooking.common.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public final class CommonSecurity {

	private CommonSecurity() {
	}

	public static HttpSecurity apply(HttpSecurity http, GatewayAuthFilter gatewayAuthFilter,
			InternalTokenFilter internalTokenFilter) throws Exception {

		return http.csrf(csrf -> csrf.disable())
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(internalTokenFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(gatewayAuthFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
