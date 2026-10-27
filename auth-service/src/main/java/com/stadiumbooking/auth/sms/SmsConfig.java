package com.stadiumbooking.auth.sms;

import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.stadiumbooking.auth.otp.OtpProperties;

@Configuration
@EnableConfigurationProperties({ SmsProperties.class, OtpProperties.class })
public class SmsConfig {

	private static final Logger log = LoggerFactory.getLogger(SmsConfig.class);

	@Bean
	public Clock clock() {

		return Clock.systemDefaultZone();
	}

	/**
	 * Picks the SMS provider from sms.provider. The service refuses to start when
	 * a real provider is chosen without its credentials, so a typo in the settings
	 * shows up at start-up and not when the first customer tries to register.
	 */
	@Bean
	public SmsSender smsSender(SmsProperties props) {

		String provider = props.getProvider() == null ? "" : props.getProvider().trim().toLowerCase();

		switch (provider) {

		case "fast2sms":

			require(props.getFast2sms().getApiKey(), "FAST2SMS_API_KEY");

			log.info("OTP texts will be sent with Fast2SMS");

			return new Fast2SmsSender(restClient(), props.getFast2sms().getApiKey().trim(),
					props.getFast2sms().getRoute().trim());

		case "twilio":

			require(props.getTwilio().getAccountSid(), "TWILIO_ACCOUNT_SID");
			require(props.getTwilio().getAuthToken(), "TWILIO_AUTH_TOKEN");
			require(props.getTwilio().getFromNumber(), "TWILIO_FROM_NUMBER");

			log.info("OTP texts will be sent with Twilio");

			return new TwilioSmsSender(restClient(), props.getTwilio().getAccountSid().trim(),
					props.getTwilio().getAuthToken().trim(), props.getTwilio().getFromNumber().trim());

		case "log":

			log.warn("sms.provider=log: OTPs are written to this log and NOT texted. Use fast2sms or twilio for real customers.");

			return new LogSmsSender();

		default:

			throw new IllegalStateException("Unknown sms.provider '" + props.getProvider()
					+ "'. Use one of: log, fast2sms, twilio");
		}
	}

	private static void require(String value, String settingName) {

		if (value == null || value.isBlank()) {
			throw new IllegalStateException("sms.provider needs " + settingName + " to be set");
		}
	}

	private static RestClient restClient() {

		JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(
				HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());

		factory.setReadTimeout(Duration.ofSeconds(10));

		return RestClient.builder().requestFactory(factory).build();
	}
}
