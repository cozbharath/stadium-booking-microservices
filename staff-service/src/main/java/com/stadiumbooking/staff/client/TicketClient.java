package com.stadiumbooking.staff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.stadiumbooking.common.dto.TicketResponse;

@FeignClient(name = "ticket-service")
public interface TicketClient {

	@PostMapping("/api/tickets/validate/{ticketNumber}")
	TicketResponse validate(@PathVariable("ticketNumber") String ticketNumber);

	@GetMapping("/api/tickets/number/{ticketNumber}")
	TicketResponse getByNumber(@PathVariable("ticketNumber") String ticketNumber);
}