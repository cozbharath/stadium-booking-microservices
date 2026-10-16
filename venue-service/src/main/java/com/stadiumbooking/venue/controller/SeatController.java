package com.stadiumbooking.venue.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.venue.dto.seat.request.BulkSeatRequest;
import com.stadiumbooking.venue.dto.seat.request.SeatRequest;
import com.stadiumbooking.venue.dto.seat.response.SeatResponse;
import com.stadiumbooking.venue.service.SeatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

	private final SeatService seatService;

	@PostMapping
	public ResponseEntity<SeatResponse> createSeat(@RequestBody SeatRequest request) {

		return new ResponseEntity<>(seatService.createSeat(request), HttpStatus.CREATED);
	}

	@PostMapping("/bulk")
	public ResponseEntity<List<SeatResponse>> createSeatsBulk(@RequestBody BulkSeatRequest request) {

		return new ResponseEntity<>(seatService.createSeatsBulk(request), HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<SeatResponse> getSeatById(@PathVariable Long id) {

		return ResponseEntity.ok(seatService.getSeatById(id));
	}

	@GetMapping
	public ResponseEntity<List<SeatResponse>> getAllSeats() {

		return ResponseEntity.ok(seatService.getAllSeats());
	}

	@GetMapping("/stand/{standId}")
	public ResponseEntity<List<SeatResponse>> getSeatsByStand(@PathVariable Long standId) {

		return ResponseEntity.ok(seatService.getSeatsByStand(standId));
	}

	@PutMapping("/{id}")
	public ResponseEntity<SeatResponse> updateSeat(@PathVariable Long id, @RequestBody SeatRequest request) {

		return ResponseEntity.ok(seatService.updateSeat(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSeat(@PathVariable Long id) {

		seatService.deleteSeat(id);

		return ResponseEntity.noContent().build();
	}
}