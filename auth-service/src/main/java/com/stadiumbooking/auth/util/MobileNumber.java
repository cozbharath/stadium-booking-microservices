package com.stadiumbooking.auth.util;

import java.util.regex.Pattern;

/**
 * Helpers for Indian mobile numbers.
 *
 * Numbers are stored as the plain 10 digits (for example 9876543210), so the
 * same number typed as "+91 98765 43210" or "09876543210" is treated as one.
 */
public final class MobileNumber {

	private static final Pattern INDIAN_MOBILE = Pattern.compile("^[6-9]\\d{9}$");

	private MobileNumber() {
	}

	/**
	 * Cleans the input and returns the 10 digit number.
	 *
	 * @throws IllegalArgumentException when the value is missing or not a valid Indian mobile number
	 */
	public static String normalize(String raw) {

		if (raw == null || raw.isBlank()) {
			throw new IllegalArgumentException("Mobile number is required");
		}

		String digits = raw.trim().replaceAll("[\\s\\-()]", "");

		if (digits.startsWith("+91")) {
			digits = digits.substring(3);
		} else if (digits.length() == 12 && digits.startsWith("91")) {
			digits = digits.substring(2);
		} else if (digits.length() == 11 && digits.startsWith("0")) {
			digits = digits.substring(1);
		}

		if (!INDIAN_MOBILE.matcher(digits).matches()) {
			throw new IllegalArgumentException("Enter a valid 10-digit mobile number");
		}

		return digits;
	}

	/** 98XXXXXX10 style value that is safe to show in a response or a log. */
	public static String mask(String mobile) {

		if (mobile == null || mobile.length() < 4) {
			return "XXXXXXXXXX";
		}

		return "XXXXXX" + mobile.substring(mobile.length() - 4);
	}
}
