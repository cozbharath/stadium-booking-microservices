package com.stadiumbooking.event.dto.eventseat.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventSeatResponse {

	private Long id;

	private Long eventId;

	private Long seatId;

	private String standName;

	private String rowNumber;

	private Integer seatNumber;

	private BigDecimal price;
}