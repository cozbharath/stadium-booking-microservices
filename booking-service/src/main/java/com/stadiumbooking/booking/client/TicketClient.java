package com.stadiumbooking.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.stadiumbooking.common.contract.CreateTicketsRequest;

@FeignClient(name = "ticket-service")
public interface TicketClient {

	@PostMapping("/internal/tickets")
	void createTickets(@RequestBody CreateTicketsRequest request);

	@PostMapping("/internal/tickets/booking/{bookingId}/cancel")
	void cancelTickets(@PathVariable("bookingId") Long bookingId);
}