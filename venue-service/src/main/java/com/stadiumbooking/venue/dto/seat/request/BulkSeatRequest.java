package com.stadiumbooking.venue.dto.seat.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkSeatRequest {

	private Long standId;

	// e.g. ["A", "B", "C", "D"]
	private List<String> rows;

	// generates seatStart..seatEnd for every row above
	private Integer seatStart;

	private Integer seatEnd;
}