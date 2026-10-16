package com.stadiumbooking.booking.util;

import java.time.LocalDate;
import java.util.List;

import com.stadiumbooking.common.contract.EventInfo;
import com.stadiumbooking.common.enums.BookingStatus;
import com.stadiumbooking.common.enums.EventStatus;
import com.stadiumbooking.common.exception.BookingException;

public final class BookingRules {

	private BookingRules() {
	}

	// statuses that make a seat unavailable to everybody else
	public static final List<BookingStatus> SEAT_BLOCKING_STATUSES = List.of(BookingStatus.PENDING,
			BookingStatus.CONFIRMED);

	// how long an unpaid booking is kept before it expires
	public static final long PENDING_BOOKING_MINUTES = 20;

	public static void assertEventBookable(EventInfo event) {

		if (event.status() == EventStatus.CANCELLED || event.status() == EventStatus.COMPLETED) {
			throw new BookingException("This event is not open for booking");
		}

		if (event.eventDate() != null && event.eventDate().isBefore(LocalDate.now())) {
			throw new BookingException("This event has already taken place");
		}
	}
}