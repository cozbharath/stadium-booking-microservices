package com.stadiumbooking.booking.dto.seathold.response;

import java.time.LocalDateTime;
import java.util.List;

import com.stadiumbooking.booking.enums.HoldStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatHoldResponse {

	private String holdToken;

	private List<Long> eventSeatIds;

	private LocalDateTime heldAt;

	private LocalDateTime expiresAt;

	private HoldStatus status;
}