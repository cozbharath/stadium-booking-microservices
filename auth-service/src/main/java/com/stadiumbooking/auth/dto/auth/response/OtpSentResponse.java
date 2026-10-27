package com.stadiumbooking.auth.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtpSentResponse {

	private String message;

	/** Seconds until the OTP stops working. */
	private int expiresInSeconds;

	/** Seconds the user must wait before asking for another OTP. */
	private int resendAfterSeconds;

	/** For display only, for example XXXXXX3210. */
	private String maskedMobile;
}
