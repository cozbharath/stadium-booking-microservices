package com.stadiumbooking.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.stadiumbooking.common.security.HeaderNames;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignForwardConfig {

	private static final List<String> FORWARDED = List.of(HeaderNames.USER_ID, HeaderNames.USER_NAME,
			HeaderNames.USER_EMAIL, HeaderNames.USER_ROLE);

	@org.springframework.beans.factory.annotation.Value("${internal.token:change-me-internal-token}")
	private String internalToken;

	@Bean
	public RequestInterceptor forwardUserAndInternalToken() {
		return template -> {
			template.header(HeaderNames.INTERNAL_TOKEN, internalToken);

			RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
			if (attrs instanceof ServletRequestAttributes servletAttrs) {
				HttpServletRequest current = servletAttrs.getRequest();
				for (String header : FORWARDED) {
					String value = current.getHeader(header);
					if (value != null) {
						template.header(header, value);
					}
				}
			}
		};
	}
}
