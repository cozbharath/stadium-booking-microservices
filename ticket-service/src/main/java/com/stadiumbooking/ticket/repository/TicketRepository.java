package com.stadiumbooking.ticket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stadiumbooking.common.enums.TicketStatus;
import com.stadiumbooking.ticket.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

	Optional<Ticket> findByTicketNumber(String ticketNumber);

	List<Ticket> findByBookingId(Long bookingId);

	List<Ticket> findByStatus(TicketStatus status);
}