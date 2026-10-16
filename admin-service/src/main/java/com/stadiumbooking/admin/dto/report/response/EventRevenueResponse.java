package com.stadiumbooking.admin.dto.report.response;

import java.math.BigDecimal;

public record EventRevenueResponse(String eventName, long confirmedBookings, BigDecimal revenue) {
}