package com.stadiumbooking.support.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.stadiumbooking.common.dto.TicketResponse;

@FeignClient(name = "ticket-service")
public interface TicketClient {

	@GetMapping("/api/tickets/booking/{bookingId}")
	List<TicketResponse> getTicketsByBooking(@PathVariable("bookingId") Long bookingId);
}