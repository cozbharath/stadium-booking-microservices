package com.stadiumbooking.booking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.booking.dto.seathold.request.SeatHoldRequest;
import com.stadiumbooking.booking.dto.seathold.response.SeatHoldResponse;
import com.stadiumbooking.booking.service.SeatHoldService;
import com.stadiumbooking.common.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/seat-holds")
@RequiredArgsConstructor
public class SeatHoldController {

	private final SeatHoldService seatHoldService;

	@PostMapping
	public ResponseEntity<SeatHoldResponse> holdSeats(@RequestBody SeatHoldRequest request,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		SeatHoldResponse response = seatHoldService.holdSeats(request, userDetails.getId());

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/{holdToken}")
	public ResponseEntity<Void> releaseHold(@PathVariable String holdToken,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		seatHoldService.releaseHold(holdToken, userDetails.getId());

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<SeatHoldResponse>> getUserHolds(@PathVariable Long userId,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		if (!userDetails.getId().equals(userId)) {
			throw new AccessDeniedException("You can only view your own seat holds");
		}

		return ResponseEntity.ok(seatHoldService.getUserHolds(userId));
	}

	@GetMapping("/event/{eventId}/unavailable")
	public ResponseEntity<List<Long>> getUnavailableSeats(@PathVariable Long eventId,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		return ResponseEntity.ok(seatHoldService.getUnavailableSeatIds(eventId, userDetails.getId()));
	}
}