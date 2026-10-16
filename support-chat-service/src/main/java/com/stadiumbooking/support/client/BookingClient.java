package com.stadiumbooking.support.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.stadiumbooking.common.dto.BookingResponse;

/**
 * The customer's own bookings. The caller's X-User-* headers are forwarded, so
 * booking-service enforces ownership.
 */
@FeignClient(name = "booking-service")
public interface BookingClient {

	@GetMapping("/api/bookings/my")
	List<BookingResponse> getMyBookings();

	@GetMapping("/api/bookings/my/{id}")
	BookingResponse getMyBooking(@PathVariable("id") Long id);
}