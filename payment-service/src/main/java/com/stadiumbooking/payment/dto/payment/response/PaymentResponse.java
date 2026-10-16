package com.stadiumbooking.payment.dto.payment.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.stadiumbooking.payment.enums.PaymentMethod;
import com.stadiumbooking.payment.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

	private Long paymentId;

	private Long bookingId;

	private BigDecimal amount;

	private PaymentMethod paymentMethod;

	private String transactionId;

	private PaymentStatus status;

	private LocalDateTime paymentDate;
}