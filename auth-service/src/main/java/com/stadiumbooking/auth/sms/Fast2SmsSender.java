package com.stadiumbooking.auth.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.stadiumbooking.auth.util.MobileNumber;

/**
 * Sends the OTP through Fast2SMS (Indian numbers).
 *
 * Request: POST https://www.fast2sms.com/dev/bulkV2
 * Header : authorization: API_KEY
 * Body   : {"route":"otp","variables_values":"123456","numbers":"9876543210"}
 *
 * Fast2SMS answers {"return":true,...} when the message was accepted.
 * If their OTP route changes, only this class needs to change.
 */
public class Fast2SmsSender implements SmsSender {

	private static final Logger log = LoggerFactory.getLogger(Fast2SmsSender.class);

	private static final String URL = "https://www.fast2sms.com/dev/bulkV2";

	private final RestClient restClient;

	private final String apiKey;

	private final String route;

	public Fast2SmsSender(RestClient restClient, String apiKey, String route) {

		if (!route.matches("[a-z_]+")) {
			throw new IllegalStateException("sms.fast2sms.route may only contain lowercase letters");
		}

		this.restClient = restClient;
		this.apiKey = apiKey;
		this.route = route;
	}

	@Override
	public void sendOtp(String mobile, String otp, int validMinutes) {

		// only digits and a fixed word go into the body, so building the JSON by hand is safe
		String body = "{\"route\":\"" + route + "\",\"variables_values\":\"" + otp + "\",\"numbers\":\"" + mobile
				+ "\"}";

		String response;

		try {

			response = restClient.post().uri(URL).header("authorization", apiKey).contentType(MediaType.APPLICATION_JSON)
					.body(body).retrieve().body(String.class);

		} catch (RestClientResponseException e) {

			log.error("Fast2SMS rejected the OTP for {}: HTTP {} {}", MobileNumber.mask(mobile), e.getStatusCode().value(),
					e.getResponseBodyAsString());

			throw new SmsDeliveryException("Could not send the OTP. Please try again.", e);

		} catch (RestClientException e) {

			log.error("Fast2SMS could not be reached for {}: {}", MobileNumber.mask(mobile), e.getMessage());

			throw new SmsDeliveryException("Could not send the OTP. Please try again.", e);
		}

		String compact = response == null ? "" : response.replaceAll("\\s", "");

		if (!compact.contains("\"return\":true")) {

			log.error("Fast2SMS did not accept the OTP for {}: {}", MobileNumber.mask(mobile), response);

			throw new SmsDeliveryException("Could not send the OTP. Please try again.");
		}
	}
}
