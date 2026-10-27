package com.stadiumbooking.auth.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stadiumbooking.auth.util.MobileNumber;

/**
 * Test sender: writes the OTP to the log instead of texting it.
 * Never use it for real customers, anyone who can read the logs can sign in.
 */
public class LogSmsSender implements SmsSender {

	private static final Logger log = LoggerFactory.getLogger(LogSmsSender.class);

	@Override
	public void sendOtp(String mobile, String otp, int validMinutes) {

		log.warn("[SMS provider=log] OTP for {} is {} (valid {} min). Nothing was sent.", MobileNumber.mask(mobile),
				otp, validMinutes);
	}
}
