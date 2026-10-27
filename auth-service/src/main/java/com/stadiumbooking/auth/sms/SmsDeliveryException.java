package com.stadiumbooking.auth.sms;

/** The SMS provider rejected the message or could not be reached. */
public class SmsDeliveryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SmsDeliveryException(String message) {
		super(message);
	}

	public SmsDeliveryException(String message, Throwable cause) {
		super(message, cause);
	}
}
