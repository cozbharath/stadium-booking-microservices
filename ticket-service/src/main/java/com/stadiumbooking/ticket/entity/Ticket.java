package com.stadiumbooking.ticket.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.stadiumbooking.common.enums.TicketStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tickets")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// was: @ManyToOne Booking booking / @ManyToOne BookingSeat bookingSeat
	// (booking-service now)
	@Column(name = "booking_id", nullable = false)
	private Long bookingId;

	@Column(name = "booking_seat_id")
	private Long bookingSeatId;

	// owner of the booking
	@Column(name = "user_id", nullable = false)
	private Long userId;

	private String customerName;

	private String eventName;

	private LocalDate eventDate;

	private LocalTime startTime;

	private String stadiumName;

	private String standName;

	@Column(name = "row_num")
	private String rowNumber;

	private Integer seatNumber;

	private BigDecimal amount;

	@Column(unique = true, nullable = false)
	private String ticketNumber;

	@Column(columnDefinition = "TEXT")
	private String qrCode;

	@Enumerated(EnumType.STRING)
	private TicketStatus status;

	private LocalDateTime issuedAt;
}