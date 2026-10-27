package com.stadiumbooking.auth.otp;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/** Settings under "otp:" in application.yml. */
@Getter
@Setter
@ConfigurationProperties(prefix = "otp")
public class OtpProperties {

	/** Key for hashing OTPs. Falls back to the JWT secret, see application.yml. */
	private String secret = "dev-only-otp-secret-change-me";

	/** Number of digits in the OTP. */
	private int length = 6;

	/** How long an OTP stays valid. */
	private int ttlSeconds = 300;

	/** Wrong guesses allowed before the OTP is thrown away. */
	private int maxAttempts = 5;

	/** Minimum gap between two OTPs for the same number. */
	private int resendCooldownSeconds = 30;

	/** OTPs one number may request inside one window. */
	private int maxSendsPerWindow = 3;

	private int windowSeconds = 600;

	/** Hard cap on OTP texts per day for the whole service, to limit SMS spend. */
	private int dailySendLimit = 300;
}
