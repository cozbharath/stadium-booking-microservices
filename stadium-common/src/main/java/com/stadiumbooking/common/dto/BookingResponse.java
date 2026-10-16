package com.stadiumbooking.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.stadiumbooking.common.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

	private Long bookingId;

	private String eventName;

	private LocalDate eventDate;

	private LocalTime startTime;

	private String stadiumName;

	private List<String> seats;

	private BigDecimal totalAmount;

	private BookingStatus status;

	private LocalDateTime bookingDate;

	private String customerName;

	private String customerEmail;

}
