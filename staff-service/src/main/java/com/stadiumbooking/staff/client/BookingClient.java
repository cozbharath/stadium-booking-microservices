package com.stadiumbooking.staff.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.stadiumbooking.common.dto.BookingResponse;

@FeignClient(name = "booking-service")
public interface BookingClient {

	@GetMapping("/api/bookings/{id}")
	BookingResponse getBooking(@PathVariable("id") Long id);

	@GetMapping("/api/bookings/user/{userName}")
	List<BookingResponse> getBookingsByUserName(@PathVariable("userName") String userName);

	@GetMapping("/api/bookings/event/{eventId}")
	List<BookingResponse> getBookingsByEvent(@PathVariable("eventId") Long eventId);
}