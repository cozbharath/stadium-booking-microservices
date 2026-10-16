package com.stadiumbooking.staff.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.common.dto.TicketResponse;
import com.stadiumbooking.staff.dto.gate.response.GateValidationResponse;
import com.stadiumbooking.staff.service.StaffGateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/staff/tickets")
@RequiredArgsConstructor
public class StaffTicketController {

	private final StaffGateService staffGateService;

	// gate scan
	@PostMapping("/validate/{ticketNumber}")
	public ResponseEntity<GateValidationResponse> validate(@PathVariable String ticketNumber) {
		return ResponseEntity.ok(staffGateService.validateTicket(ticketNumber));
	}

	// look a ticket up without consuming it
	@GetMapping("/{ticketNumber}")
	public ResponseEntity<TicketResponse> lookup(@PathVariable String ticketNumber) {
		return ResponseEntity.ok(staffGateService.lookupTicket(ticketNumber));
	}
}