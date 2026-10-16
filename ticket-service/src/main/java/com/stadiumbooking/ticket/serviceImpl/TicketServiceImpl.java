package com.stadiumbooking.ticket.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stadiumbooking.common.contract.CreateTicketsRequest;
import com.stadiumbooking.common.contract.InternalSeatData;
import com.stadiumbooking.common.dto.TicketResponse;
import com.stadiumbooking.common.enums.TicketStatus;
import com.stadiumbooking.common.exception.ResourceNotFoundException;
import com.stadiumbooking.ticket.entity.Ticket;
import com.stadiumbooking.ticket.repository.TicketRepository;
import com.stadiumbooking.ticket.service.TicketService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

	private final TicketRepository ticketRepository;

	@Override
	@Transactional
	public List<TicketResponse> createTickets(CreateTicketsRequest request) {

		Long bookingId = request.bookingId();

		// already issued -> return them (booking-service may retry)
		List<Ticket> existingTickets = ticketRepository.findByBookingId(bookingId);

		if (!existingTickets.isEmpty()) {
			return existingTickets.stream().map(this::mapToResponse).toList();
		}

		if (request.seats() == null || request.seats().isEmpty()) {
			throw new IllegalArgumentException("Booking has no seats to issue tickets for");
		}

		List<Ticket> tickets = new ArrayList<>();

		for (InternalSeatData seat : request.seats()) {

			String ticketNumber = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

			String qrCode = "TICKET:" + ticketNumber + "|BOOKING:" + bookingId;

			tickets.add(Ticket.builder().bookingId(bookingId).bookingSeatId(seat.bookingSeatId())
					.userId(request.userId()).customerName(request.customerName()).eventName(request.eventName())
					.eventDate(request.eventDate()).startTime(request.startTime()).stadiumName(request.stadiumName())
					.standName(seat.standName()).rowNumber(seat.rowNumber()).seatNumber(seat.seatNumber())
					.amount(seat.price()).ticketNumber(ticketNumber).qrCode(qrCode).status(TicketStatus.ACTIVE)
					.issuedAt(LocalDateTime.now()).build());
		}

		return ticketRepository.saveAll(tickets).stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public TicketResponse getTicketById(Long id, Long userId, boolean isStaffOrAdmin) {

		Ticket ticket = ticketRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

		assertOwnerOrStaff(ticket, userId, isStaffOrAdmin);

		return mapToResponse(ticket);
	}

	@Override
	@Transactional(readOnly = true)
	public TicketResponse getTicketByNumber(String ticketNumber, Long userId, boolean isStaffOrAdmin) {

		Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
				.orElseThrow(() -> new ResourceNotFoundException("Ticket not found with number: " + ticketNumber));

		assertOwnerOrStaff(ticket, userId, isStaffOrAdmin);

		return mapToResponse(ticket);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TicketResponse> getTicketsByBooking(Long bookingId, Long userId, boolean isStaffOrAdmin) {

		List<Ticket> tickets = ticketRepository.findByBookingId(bookingId);

		for (Ticket ticket : tickets) {
			if (!isStaffOrAdmin && !ticket.getUserId().equals(userId)) {
				throw new AccessDeniedException("You cannot view tickets for another user's booking");
			}
		}

		return tickets.stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional
	public TicketResponse validateTicket(String ticketNumber) {

		Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
				.orElseThrow(() -> new ResourceNotFoundException("Ticket not found with number: " + ticketNumber));

		if (ticket.getStatus() == TicketStatus.CANCELLED) {
			throw new IllegalArgumentException("This ticket was cancelled and cannot be used");
		}

		if (ticket.getStatus() == TicketStatus.USED) {
			throw new IllegalArgumentException("This ticket has already been used");
		}

		ticket.setStatus(TicketStatus.USED);

		return mapToResponse(ticketRepository.save(ticket));
	}

	@Override
	@Transactional
	public void cancelTicketsByBooking(Long bookingId) {

		List<Ticket> tickets = ticketRepository.findByBookingId(bookingId);

		for (Ticket ticket : tickets) {
			ticket.setStatus(TicketStatus.CANCELLED);
		}

		ticketRepository.saveAll(tickets);
	}

	private void assertOwnerOrStaff(Ticket ticket, Long userId, boolean isStaffOrAdmin) {

		if (isStaffOrAdmin) {
			return;
		}

		if (!ticket.getUserId().equals(userId)) {
			throw new AccessDeniedException("You cannot view another user's ticket");
		}
	}

	private TicketResponse mapToResponse(Ticket ticket) {
		return new TicketResponse(ticket.getId(), ticket.getTicketNumber(), ticket.getEventName(),
				ticket.getEventDate(), ticket.getStartTime(), ticket.getStadiumName(), ticket.getStandName(),
				ticket.getRowNumber(), ticket.getSeatNumber(), ticket.getAmount(), ticket.getQrCode(),
				ticket.getStatus(), ticket.getIssuedAt(), ticket.getBookingId(), ticket.getCustomerName());
	}
}