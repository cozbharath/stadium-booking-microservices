package com.stadiumbooking.auth.sms;

/** Delivers a one time password to a phone. One implementation per provider. */
public interface SmsSender {

	/**
	 * @param mobile        10 digit Indian mobile number
	 * @param otp           the code to deliver
	 * @param validMinutes  how long the code stays valid, for the message text
	 * @throws SmsDeliveryException when the provider could not send the message
	 */
	void sendOtp(String mobile, String otp, int validMinutes);
}
