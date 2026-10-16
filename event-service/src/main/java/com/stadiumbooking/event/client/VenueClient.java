package com.stadiumbooking.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.stadiumbooking.common.contract.SeatInfo;
import com.stadiumbooking.common.contract.StadiumInfo;

/** Calls venue-service (found by its name in Eureka). */
@FeignClient(name = "venue-service")
public interface VenueClient {

	@GetMapping("/internal/stadiums/{id}")
	StadiumInfo getStadium(@PathVariable("id") Long id);

	@GetMapping("/internal/seats/{id}")
	SeatInfo getSeat(@PathVariable("id") Long id);
}