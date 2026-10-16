package com.stadiumbooking.admin.dto.report.response;

import java.math.BigDecimal;

public record EventBookingSummaryResponse(Long eventId, String eventName, long totalBookings, long confirmedBookings,
		long seatsSold, BigDecimal revenue) {
}