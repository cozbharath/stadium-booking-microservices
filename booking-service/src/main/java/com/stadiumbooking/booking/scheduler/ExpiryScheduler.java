package com.stadiumbooking.booking.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stadiumbooking.booking.service.BookingService;
import com.stadiumbooking.booking.service.SeatHoldService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpiryScheduler {

	private final SeatHoldService seatHoldService;
	private final BookingService bookingService;

	@Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
	public void expireStaleReservations() {
		seatHoldService.expireHolds();
		bookingService.expirePendingBookings();
	}
}