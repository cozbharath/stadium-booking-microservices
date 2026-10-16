package com.stadiumbooking.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.stadiumbooking.common.enums.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {

	private Long ticketId;

	private String ticketNumber;

	private String eventName;

	private LocalDate eventDate;

	private LocalTime startTime;

	private String stadiumName;

	private String standName;

	private String rowNumber;

	private Integer seatNumber;

	private BigDecimal amount;

	private String qrCode;

	private TicketStatus status;

	private LocalDateTime issuedAt;

	private Long bookingId;

	private String customerName;
}
