package com.stadiumbooking.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("auth-service", r -> r.path("/api/auth/**", "/api/users/**").uri("lb://auth-service"))
				.route("venue-service",
						r -> r.path("/api/stadiums/**", "/api/stands/**", "/api/stand-types/**", "/api/seats/**")
								.uri("lb://venue-service"))
				.route("event-service", r -> r.path("/api/events/**", "/api/event-seats/**").uri("lb://event-service"))
				.route("booking-service",
						r -> r.path("/api/seat-holds/**", "/api/bookings/**").uri("lb://booking-service"))
				.route("payment-service", r -> r.path("/api/payments/**").uri("lb://payment-service"))
				.route("ticket-service", r -> r.path("/api/tickets/**").uri("lb://ticket-service"))
				.route("support-chat-service", r -> r.path("/api/support/**").uri("lb://support-chat-service"))
				.route("admin-service", r -> r.path("/api/admin/**").uri("lb://admin-service"))
				.route("staff-service", r -> r.path("/api/staff/**").uri("lb://staff-service")).build();
	}
}
