package com.stadiumbooking.venue.dto.seat.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {

	private Long id;

	private Long standId;

	private String standName;

	private String rowNumber;

	private Integer seatNumber;
}