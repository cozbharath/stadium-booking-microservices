package com.stadiumbooking.common.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class InternalTokenFilter extends OncePerRequestFilter {

	@Value("${internal.token:change-me-internal-token}")
	private String internalToken;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		if (request.getRequestURI().startsWith("/internal/")) {
			String sent = request.getHeader(HeaderNames.INTERNAL_TOKEN);
			if (sent == null || !sent.equals(internalToken)) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Internal endpoint");
				return;
			}
			SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("internal",
					null, List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))));
		}

		chain.doFilter(request, response);
	}
}
