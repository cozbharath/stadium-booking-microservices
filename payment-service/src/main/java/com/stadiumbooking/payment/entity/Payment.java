package com.stadiumbooking.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.stadiumbooking.payment.enums.PaymentMethod;
import com.stadiumbooking.payment.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// was: @ManyToOne Booking booking (Booking lives in booking-service now)
	@Column(name = "booking_id", nullable = false)
	private Long bookingId;

	// owner of the booking; replaces the old findByBooking_UserId join
	@Column(name = "user_id", nullable = false)
	private Long userId;

	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@Column(unique = true)
	private String transactionId;

	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	private LocalDateTime paymentDate;
}