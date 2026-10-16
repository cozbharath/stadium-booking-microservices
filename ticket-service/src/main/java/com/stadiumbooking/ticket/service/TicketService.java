package com.stadiumbooking.ticket.service;

import java.util.List;

import com.stadiumbooking.common.contract.CreateTicketsRequest;
import com.stadiumbooking.common.dto.TicketResponse;

public interface TicketService {

	// called by booking-service (through /internal/tickets) when a booking is
	// confirmed
	List<TicketResponse> createTickets(CreateTicketsRequest request);

	TicketResponse getTicketById(Long id, Long userId, boolean isStaffOrAdmin);

	TicketResponse getTicketByNumber(String ticketNumber, Long userId, boolean isStaffOrAdmin);

	List<TicketResponse> getTicketsByBooking(Long bookingId, Long userId, boolean isStaffOrAdmin);

	// gate scan: marks the ticket USED
	TicketResponse validateTicket(String ticketNumber);

	// called by booking-service when a confirmed booking is cancelled
	void cancelTicketsByBooking(Long bookingId);
}