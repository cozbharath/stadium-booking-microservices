package com.stadiumbooking.event.dto.eventseat.request;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkEventSeatRequest {

	private Long eventId;

	private List<Long> seatIds;

	private BigDecimal price;
}