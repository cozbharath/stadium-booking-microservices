package com.stadiumbooking.event.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.common.contract.EventSeatInfo;
import com.stadiumbooking.event.repository.EventSeatRepository;

import lombok.RequiredArgsConstructor;

/**
 * Used by booking-service to read seat prices and labels in one call. Not
 * routed by the gateway.
 */
@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalEventController {

	private final EventSeatRepository eventSeatRepository;

	@GetMapping("/event-seats")
	public List<EventSeatInfo> getEventSeats(@RequestParam("ids") List<Long> ids) {
		return eventSeatRepository
				.findAllById(ids).stream().map(es -> new EventSeatInfo(es.getId(), es.getEvent().getId(),
						es.getSeatId(), es.getStandName(), es.getRowNumber(), es.getSeatNumber(), es.getPrice()))
				.toList();
	}
}