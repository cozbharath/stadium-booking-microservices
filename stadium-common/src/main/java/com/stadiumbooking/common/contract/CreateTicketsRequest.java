package com.stadiumbooking.common.contract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateTicketsRequest(Long bookingId, Long userId, String customerName, String eventName,
		LocalDate eventDate, LocalTime startTime, String stadiumName, BigDecimal totalAmount,
		List<InternalSeatData> seats) {
}