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

import com.stadiumbooking.event.dto.event.request.EventRequest;
import com.stadiumbooking.event.dto.event.response.EventResponse;
import com.stadiumbooking.event.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

	private final EventService eventService;

	@PostMapping
	public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest request) {

		return new ResponseEntity<>(eventService.createEvent(request), HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {

		return ResponseEntity.ok(eventService.getEventById(id));
	}

	@GetMapping
	public ResponseEntity<List<EventResponse>> getAllEvents() {

		return ResponseEntity.ok(eventService.getAllEvents());
	}

	@GetMapping("/upcoming")
	public ResponseEntity<List<EventResponse>> getUpcomingEvents() {

		return ResponseEntity.ok(eventService.getUpcomingEvents());
	}

	@GetMapping("/stadium/{stadiumId}")
	public ResponseEntity<List<EventResponse>> getEventsByStadium(@PathVariable Long stadiumId) {

		return ResponseEntity.ok(eventService.getEventsByStadium(stadiumId));
	}

	@PutMapping("/{id}")
	public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, @RequestBody EventRequest request) {

		return ResponseEntity.ok(eventService.updateEvent(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {

		eventService.deleteEvent(id);

		return ResponseEntity.noContent().build();
	}
}