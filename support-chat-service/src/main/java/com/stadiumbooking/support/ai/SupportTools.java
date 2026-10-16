package com.stadiumbooking.support.ai;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.stadiumbooking.common.dto.BookingResponse;
import com.stadiumbooking.common.dto.TicketResponse;
import com.stadiumbooking.support.client.BookingClient;
import com.stadiumbooking.support.client.TicketClient;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

/**
 * Same tools as the monolith, but the data now comes from booking-service and
 * ticket-service. Those services only return the logged-in customer's own data
 * (the user headers are forwarded), so the "only my own bookings" guarantee is
 * kept.
 */
@Component
@RequiredArgsConstructor
public class SupportTools {

	private final BookingClient bookingClient;
	private final TicketClient ticketClient;

	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

	@Tool(description = "Get the current logged-in customer's own bookings: id, event name, event date, "
			+ "status (PENDING, CONFIRMED, CANCELLED, EXPIRED), number of seats and total amount.")
	public List<BookingSummary> getMyBookings() {
		return bookingClient.getMyBookings().stream().map(this::toSummary).toList();
	}

	@Tool(description = "Get full details of one of the current customer's bookings by booking id, "
			+ "including seat numbers and payment status. Only works for the current customer's own bookings.")
	public BookingDetail getBookingDetails(@ToolParam(description = "The numeric booking id, e.g. 12") Long bookingId) {

		BookingResponse booking;

		try {
			booking = bookingClient.getMyBooking(bookingId);
		} catch (FeignException e) {
			// do not leak whether a booking exists at all to someone else
			throw new AccessDeniedException("Booking not found with id: " + bookingId);
		}

		List<String> seatLabels = booking.getSeats() == null ? List.of() : booking.getSeats();

		return new BookingDetail(booking.getBookingId(), booking.getEventName(), booking.getStadiumName(),
				booking.getEventDate() == null ? null : booking.getEventDate().format(DATE_FMT),
				booking.getStatus().name(), seatLabels, booking.getTotalAmount());
	}

	@Tool(description = "Get the current logged-in customer's tickets: ticket number, event name, "
			+ "seat, and status (ACTIVE, USED, CANCELLED).")
	public List<TicketSummary> getMyTickets() {
		return bookingClient.getMyBookings().stream()
				.flatMap(booking -> ticketClient.getTicketsByBooking(booking.getBookingId()).stream())
				.map(this::toSummary).toList();
	}

	private BookingSummary toSummary(BookingResponse booking) {

		int seatCount = booking.getSeats() == null ? 0 : booking.getSeats().size();

		return new BookingSummary(booking.getBookingId(), booking.getEventName(),
				booking.getEventDate() == null ? null : booking.getEventDate().format(DATE_FMT),
				booking.getStatus().name(), seatCount, booking.getTotalAmount());
	}

	private TicketSummary toSummary(TicketResponse ticket) {

		String seat = ticket.getStandName() == null ? "N/A"
				: ticket.getStandName() + " - Row " + ticket.getRowNumber() + " Seat " + ticket.getSeatNumber();

		return new TicketSummary(ticket.getTicketNumber(), ticket.getEventName(), seat, ticket.getStatus().name());
	}

	public record BookingSummary(Long bookingId, String eventName, String eventDate, String status, int seatCount,
			BigDecimal totalAmount) {
	}

	public record BookingDetail(Long bookingId, String eventName, String stadiumName, String eventDate, String status,
			List<String> seats, BigDecimal totalAmount) {
	}

	public record TicketSummary(String ticketNumber, String eventName, String seat, String status) {
	}
}