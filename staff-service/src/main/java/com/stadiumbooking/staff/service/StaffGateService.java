package com.stadiumbooking.staff.service;

import java.util.List;

import com.stadiumbooking.common.dto.BookingResponse;
import com.stadiumbooking.common.dto.TicketResponse;
import com.stadiumbooking.staff.dto.gate.response.GateValidationResponse;

public interface StaffGateService {

	GateValidationResponse validateTicket(String ticketNumber);

	TicketResponse lookupTicket(String ticketNumber);

	BookingResponse getBooking(Long id);

	List<BookingResponse> getBookingsByUserName(String userName);

	List<BookingResponse> getBookingsByEvent(Long eventId);
}