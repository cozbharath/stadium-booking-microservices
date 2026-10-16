package com.stadiumbooking.event.dto.eventseat.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventSeatRequest {

	private Long eventId;

	private Long seatId;

	private BigDecimal price;
}