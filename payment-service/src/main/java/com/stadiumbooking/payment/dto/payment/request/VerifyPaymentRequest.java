package com.stadiumbooking.payment.dto.payment.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPaymentRequest {

	private Long bookingId;

	private String razorpayOrderId;

	private String razorpayPaymentId;

	private String razorpaySignature;
}