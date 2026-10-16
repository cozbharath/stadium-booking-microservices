package com.stadiumbooking.payment.dto.payment.request;

import java.math.BigDecimal;

import com.stadiumbooking.payment.enums.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

	private Long bookingId;

	private BigDecimal amount;

	private PaymentMethod paymentMethod;
}