package com.stadiumbooking.booking.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.stadiumbooking.common.contract.EventInfo;
import com.stadiumbooking.common.contract.EventSeatInfo;

@FeignClient(name = "event-service")
public interface EventClient {

	@GetMapping("/api/events/{id}")
	EventInfo getEvent(@PathVariable("id") Long id);

	@GetMapping("/internal/event-seats")
	List<EventSeatInfo> getEventSeats(@RequestParam("ids") List<Long> ids);
}