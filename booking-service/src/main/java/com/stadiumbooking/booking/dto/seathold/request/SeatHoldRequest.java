package com.stadiumbooking.booking.dto.seathold.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatHoldRequest {

	private Long eventId;

	private List<Long> eventSeatIds;
}