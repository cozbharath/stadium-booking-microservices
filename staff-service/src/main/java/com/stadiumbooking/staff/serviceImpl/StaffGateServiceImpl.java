package com.stadiumbooking.staff.serviceImpl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.stadiumbooking.common.dto.BookingResponse;
import com.stadiumbooking.common.dto.TicketResponse;
import com.stadiumbooking.common.exception.ResourceNotFoundException;
import com.stadiumbooking.staff.client.BookingClient;
import com.stadiumbooking.staff.client.TicketClient;
import com.stadiumbooking.staff.dto.gate.response.GateValidationResponse;
import com.stadiumbooking.staff.service.StaffGateService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffGateServiceImpl implements StaffGateService {

	private static final Pattern MESSAGE = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]*)\"");

	private final TicketClient ticketClient;
	private final BookingClient bookingClient;

	@Override
	public GateValidationResponse validateTicket(String ticketNumber) {

		try {
			TicketResponse ticket = ticketClient.validate(ticketNumber);
			return new GateValidationResponse(true, "Ticket is valid - entry allowed", ticket);

		} catch (FeignException.NotFound e) {
			return new GateValidationResponse(false, "Ticket not found", null);

		} catch (FeignException.BadRequest e) {
			// ticket-service says why: already used / cancelled
			Matcher m = MESSAGE.matcher(e.contentUTF8());
			return new GateValidationResponse(false, m.find() ? m.group(1) : "Ticket cannot be used", null);
		}
	}

	@Override
	public TicketResponse lookupTicket(String ticketNumber) {
		try {
			return ticketClient.getByNumber(ticketNumber);
		} catch (FeignException.NotFound e) {
			throw new ResourceNotFoundException("Ticket not found with number: " + ticketNumber);
		}
	}

	@Override
	public BookingResponse getBooking(Long id) {
		try {
			return bookingClient.getBooking(id);
		} catch (FeignException.NotFound e) {
			throw new ResourceNotFoundException("Booking not found with id: " + id);
		}
	}

	@Override
	public List<BookingResponse> getBookingsByUserName(String userName) {
		return bookingClient.getBookingsByUserName(userName);
	}

	@Override
	public List<BookingResponse> getBookingsByEvent(Long eventId) {
		return bookingClient.getBookingsByEvent(eventId);
	}
}