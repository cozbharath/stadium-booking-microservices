package com.stadiumbooking.auth.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.stadiumbooking.auth.util.MobileNumber;

/**
 * Sends the OTP through the Twilio Messages API.
 *
 * Note: a Twilio trial account can only text verified numbers, and at the time
 * of writing only verified US numbers, so use a paid account for Indian customers.
 */
public class TwilioSmsSender implements SmsSender {

	private static final Logger log = LoggerFactory.getLogger(TwilioSmsSender.class);

	private final RestClient restClient;

	private final String accountSid;

	private final String authToken;

	private final String fromNumber;

	public TwilioSmsSender(RestClient restClient, String accountSid, String authToken, String fromNumber) {

		this.restClient = restClient;
		this.accountSid = accountSid;
		this.authToken = authToken;
		this.fromNumber = fromNumber;
	}

	@Override
	public void sendOtp(String mobile, String otp, int validMinutes) {

		String url = "https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json";

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

		form.add("To", "+91" + mobile);
		form.add("From", fromNumber);
		form.add("Body", otp + " is your BookNow verification code. It is valid for " + validMinutes
				+ " minutes. Do not share it with anyone.");

		try {

			restClient.post().uri(url).headers(headers -> headers.setBasicAuth(accountSid, authToken))
					.contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form).retrieve().toBodilessEntity();

		} catch (RestClientResponseException e) {

			log.error("Twilio rejected the OTP for {}: HTTP {} {}", MobileNumber.mask(mobile), e.getStatusCode().value(),
					e.getResponseBodyAsString());

			throw new SmsDeliveryException("Could not send the OTP. Please try again.", e);

		} catch (RestClientException e) {

			log.error("Twilio could not be reached for {}: {}", MobileNumber.mask(mobile), e.getMessage());

			throw new SmsDeliveryException("Could not send the OTP. Please try again.", e);
		}
	}
}
