package com.stadiumbooking.admin.dto.report.response;

import java.math.BigDecimal;
import java.util.List;

public record RevenueReportResponse(BigDecimal totalRevenue, long confirmedBookings, long pendingBookings,
		long cancelledBookings, long expiredBookings, List<EventRevenueResponse> revenueByEvent) {
}