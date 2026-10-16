package com.stadiumbooking.common.security;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GatewayAuthFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String id = request.getHeader(HeaderNames.USER_ID);
		String role = request.getHeader(HeaderNames.USER_ROLE);

		if (id != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				CustomUserDetails user = new CustomUserDetails(Long.valueOf(id),
						decode(request.getHeader(HeaderNames.USER_NAME)), request.getHeader(HeaderNames.USER_EMAIL),
						null, role);
				SecurityContextHolder.getContext()
						.setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
			} catch (NumberFormatException ignored) {
				// malformed header -> stay unauthenticated
			}
		}

		chain.doFilter(request, response);
	}

	private String decode(String value) {
		return value == null ? null : URLDecoder.decode(value, StandardCharsets.UTF_8);
	}
}
