package com.stadiumbooking.admin.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.stadiumbooking.common.dto.BookingResponse;

/**
 * booking-service's STAFF/ADMIN endpoints. The admin's headers are forwarded,
 * so the role check still applies.
 */
@FeignClient(name = "booking-service")
public interface BookingClient {

	@GetMapping("/api/bookings/all")
	List<BookingResponse> getAllBookings();

	@GetMapping("/api/bookings/user/{userName}")
	List<BookingResponse> getBookingsByUserName(@PathVariable("userName") String userName);

	@GetMapping("/api/bookings/event/{eventId}")
	List<BookingResponse> getBookingsByEvent(@PathVariable("eventId") Long eventId);
}