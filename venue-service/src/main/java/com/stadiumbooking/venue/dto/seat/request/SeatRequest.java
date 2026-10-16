package com.stadiumbooking.venue.dto.seat.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatRequest {

	private Long standId;

	private String rowNumber;

	private Integer seatNumber;
}