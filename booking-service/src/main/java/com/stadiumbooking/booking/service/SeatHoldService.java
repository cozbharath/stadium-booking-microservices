package com.stadiumbooking.booking.service;

import java.util.List;

import com.stadiumbooking.booking.dto.seathold.request.SeatHoldRequest;
import com.stadiumbooking.booking.dto.seathold.response.SeatHoldResponse;

public interface SeatHoldService {

	SeatHoldResponse holdSeats(SeatHoldRequest request, Long userId);

	void releaseHold(String holdToken, Long userId);

	List<SeatHoldResponse> getUserHolds(Long userId);

	void expireHolds();

	List<Long> getUnavailableSeatIds(Long eventId, Long userId);
}