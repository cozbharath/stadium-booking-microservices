package com.stadiumbooking.auth.otp;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Creates OTP codes and hashes them. Plain Java on purpose, so the rules are
 * easy to read and to test.
 */
public final class OtpCodec {

	private static final SecureRandom RANDOM = new SecureRandom();

	private OtpCodec() {
	}

	/** A random code of the given length, with leading zeros kept ("004821"). */
	public static String generate(int length) {

		if (length < 4 || length > 9) {
			throw new IllegalArgumentException("OTP length must be between 4 and 9 digits");
		}

		int bound = 1;

		for (int i = 0; i < length; i++) {
			bound *= 10;
		}

		return String.format("%0" + length + "d", RANDOM.nextInt(bound));
	}

	/**
	 * Keyed hash of the OTP, tied to the number and purpose it was issued for,
	 * so a hash from one row can never be replayed on another.
	 */
	public static String hash(String secret, String mobile, String purpose, String otp) {

		try {

			Mac mac = Mac.getInstance("HmacSHA256");

			mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

			byte[] digest = mac.doFinal((mobile + ":" + purpose + ":" + otp).getBytes(StandardCharsets.UTF_8));

			return HexFormat.of().formatHex(digest);

		} catch (GeneralSecurityException e) {

			throw new IllegalStateException("Could not hash the OTP", e);
		}
	}

	/** Compares two hashes without leaking, through timing, how many characters matched. */
	public static boolean matches(String expectedHash, String actualHash) {

		if (expectedHash == null || actualHash == null) {
			return false;
		}

		return MessageDigest.isEqual(expectedHash.getBytes(StandardCharsets.UTF_8),
				actualHash.getBytes(StandardCharsets.UTF_8));
	}
}
