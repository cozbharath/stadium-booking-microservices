package com.stadiumbooking.payment.dto.payment.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayOrderResponse {

	private String razorpayOrderId;

	private Long bookingId;

	private BigDecimal amount;

	private String currency;

	private String keyId;
}