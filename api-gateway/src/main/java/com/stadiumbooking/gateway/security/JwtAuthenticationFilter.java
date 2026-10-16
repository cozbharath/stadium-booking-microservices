package com.stadiumbooking.gateway.security;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

	private static final String USER_ID = "X-User-Id";
	private static final String USER_NAME = "X-User-Name";
	private static final String USER_EMAIL = "X-User-Email";
	private static final String USER_ROLE = "X-User-Role";
	private static final String INTERNAL_TOKEN = "X-Internal-Token";

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		ServerHttpRequest.Builder builder = exchange.getRequest().mutate().headers(h -> {
			h.remove(USER_ID);
			h.remove(USER_NAME);
			h.remove(USER_EMAIL);
			h.remove(USER_ROLE);
			h.remove(INTERNAL_TOKEN);
		});

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			try {
				Claims claims = jwtService.parse(authHeader.substring(7));
				Object userId = claims.get("userId");
				Object role = claims.get("role");
				if (userId != null && role != null) {
					String name = String.valueOf(claims.getOrDefault("name", ""));
					builder.header(USER_ID, String.valueOf(userId));
					builder.header(USER_NAME, URLEncoder.encode(name, StandardCharsets.UTF_8));
					builder.header(USER_EMAIL, claims.getSubject());
					builder.header(USER_ROLE, String.valueOf(role));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return chain.filter(exchange.mutate().request(builder.build()).build());
	}

	@Override
	public int getOrder() {
		return -1;
	}
}