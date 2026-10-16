package com.stadiumbooking.venue.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.common.contract.SeatInfo;
import com.stadiumbooking.common.contract.StadiumInfo;
import com.stadiumbooking.venue.dto.seat.response.SeatResponse;
import com.stadiumbooking.venue.dto.stadium.response.StadiumResponse;
import com.stadiumbooking.venue.service.SeatService;
import com.stadiumbooking.venue.service.StadiumService;

import lombok.RequiredArgsConstructor;

/**
 * Service-to-service lookups used by event-service. Not routed by the gateway.
 */
@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalVenueController {

	private final StadiumService stadiumService;
	private final SeatService seatService;

	@GetMapping("/stadiums/{id}")
	public StadiumInfo getStadium(@PathVariable Long id) {
		StadiumResponse s = stadiumService.getStadiumById(id);
		return new StadiumInfo(s.getId(), s.getName(), s.getLocation(), s.getCapacity());
	}

	@GetMapping("/seats/{id}")
	public SeatInfo getSeat(@PathVariable Long id) {
		SeatResponse s = seatService.getSeatById(id);
		return new SeatInfo(s.getId(), s.getStandId(), s.getStandName(), s.getRowNumber(), s.getSeatNumber());
	}
}