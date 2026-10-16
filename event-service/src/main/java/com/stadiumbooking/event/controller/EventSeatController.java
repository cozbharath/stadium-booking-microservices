package com.stadiumbooking.event.controller;

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

import com.stadiumbooking.event.dto.eventseat.request.BulkEventSeatRequest;
import com.stadiumbooking.event.dto.eventseat.request.EventSeatRequest;
import com.stadiumbooking.event.dto.eventseat.response.EventSeatResponse;
import com.stadiumbooking.event.service.EventSeatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/event-seats")
@RequiredArgsConstructor
public class EventSeatController {

	private final EventSeatService eventSeatService;

	@PostMapping
	public ResponseEntity<EventSeatResponse> createEventSeat(@RequestBody EventSeatRequest request) {

		return new ResponseEntity<>(eventSeatService.createEventSeat(request), HttpStatus.CREATED);
	}

	@PostMapping("/bulk")
	public ResponseEntity<List<EventSeatResponse>> createEventSeatsBulk(@RequestBody BulkEventSeatRequest request) {

		return new ResponseEntity<>(eventSeatService.createEventSeatsBulk(request), HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EventSeatResponse> getEventSeatById(@PathVariable Long id) {

		return ResponseEntity.ok(eventSeatService.getEventSeatById(id));
	}

	@GetMapping
	public ResponseEntity<List<EventSeatResponse>> getAllEventSeats() {

		return ResponseEntity.ok(eventSeatService.getAllEventSeats());
	}

	@GetMapping("/event/{eventId}")
	public ResponseEntity<List<EventSeatResponse>> getEventSeatsByEvent(@PathVariable Long eventId) {

		return ResponseEntity.ok(eventSeatService.getEventSeatsByEvent(eventId));
	}

	@PutMapping("/{id}")
	public ResponseEntity<EventSeatResponse> updateEventSeat(@PathVariable Long id,
			@RequestBody EventSeatRequest request) {

		return ResponseEntity.ok(eventSeatService.updateEventSeat(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEventSeat(@PathVariable Long id) {

		eventSeatService.deleteEventSeat(id);

		return ResponseEntity.noContent().build();
	}
}