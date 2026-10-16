package com.stadiumbooking.admin.service;

import java.util.List;

import com.stadiumbooking.admin.dto.report.response.EventBookingSummaryResponse;
import com.stadiumbooking.admin.dto.report.response.RevenueReportResponse;
import com.stadiumbooking.common.dto.BookingResponse;

public interface AdminReportService {

	List<BookingResponse> getAllBookings();

	List<BookingResponse> getBookingsByUserName(String userName);

	List<BookingResponse> getBookingsByEvent(Long eventId);

	RevenueReportResponse getRevenueReport();

	EventBookingSummaryResponse getEventSummary(Long eventId);
}