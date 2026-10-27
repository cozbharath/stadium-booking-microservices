package com.stadiumbooking.auth.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/** Settings under "sms:" in application.yml. */
@Getter
@Setter
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

	/** log, fast2sms or twilio. "log" prints the OTP in the service log and sends nothing. */
	private String provider = "log";

	private Twilio twilio = new Twilio();

	private Fast2Sms fast2sms = new Fast2Sms();

	@Getter
	@Setter
	public static class Twilio {

		private String accountSid = "";

		private String authToken = "";

		/** Your Twilio phone number in +E.164 format. */
		private String fromNumber = "";
	}

	@Getter
	@Setter
	public static class Fast2Sms {

		private String apiKey = "";

		/** Fast2SMS route used for OTP messages. */
		private String route = "otp";
	}
}
