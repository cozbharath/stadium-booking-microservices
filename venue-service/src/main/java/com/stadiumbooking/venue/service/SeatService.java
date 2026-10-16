package com.stadiumbooking.venue.service;

import java.util.List;

import com.stadiumbooking.venue.dto.seat.request.BulkSeatRequest;
import com.stadiumbooking.venue.dto.seat.request.SeatRequest;
import com.stadiumbooking.venue.dto.seat.response.SeatResponse;

public interface SeatService {

	SeatResponse createSeat(SeatRequest request);

	SeatResponse getSeatById(Long id);

	List<SeatResponse> getAllSeats();

	List<SeatResponse> getSeatsByStand(Long standId);

	SeatResponse updateSeat(Long id, SeatRequest request);

	void deleteSeat(Long id);

	List<SeatResponse> createSeatsBulk(BulkSeatRequest request);
}