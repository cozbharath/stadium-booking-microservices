package com.stadiumbooking.booking.dto.booking.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

	private Long eventId;

	private List<Long> eventSeatIds;

	private String holdToken;
}