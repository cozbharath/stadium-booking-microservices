package com.stadiumbooking.auth.otp;

public enum OtpPurpose {

	/** Verifies the mobile number while a customer registers. */
	REGISTER,

	/** One time password used by a customer to sign in. */
	LOGIN
}
