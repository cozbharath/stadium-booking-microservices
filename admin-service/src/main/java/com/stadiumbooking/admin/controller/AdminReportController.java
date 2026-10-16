package com.stadiumbooking.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.admin.dto.report.response.EventBookingSummaryResponse;
import com.stadiumbooking.admin.dto.report.response.RevenueReportResponse;
import com.stadiumbooking.admin.service.AdminReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

	private final AdminReportService adminReportService;

	@GetMapping("/revenue")
	public ResponseEntity<RevenueReportResponse> getRevenueReport() {
		return ResponseEntity.ok(adminReportService.getRevenueReport());
	}

	@GetMapping("/events/{eventId}")
	public ResponseEntity<EventBookingSummaryResponse> getEventSummary(@PathVariable Long eventId) {
		return ResponseEntity.ok(adminReportService.getEventSummary(eventId));
	}
}