package com.stadiumbooking.common.contract;

import java.math.BigDecimal;

import com.stadiumbooking.common.enums.BookingStatus;

/** What payment-service needs to know about a booking. */
public record InternalBookingResponse(Long bookingId, Long userId, Long eventId, BookingStatus status,
		BigDecimal totalAmount, String razorpayOrderId, String customerName, String customerEmail, String eventName) {
}