package com.stadiumbooking.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.stadiumbooking.common.contract.InternalBookingResponse;
import com.stadiumbooking.common.contract.RazorpayOrderUpdate;

@FeignClient(name = "booking-service")
public interface BookingClient {

	@GetMapping("/internal/bookings/{id}")
	InternalBookingResponse getBooking(@PathVariable("id") Long id);

	@PutMapping("/internal/bookings/{id}/razorpay-order")
	void setRazorpayOrder(@PathVariable("id") Long id, @RequestBody RazorpayOrderUpdate body);

	@PostMapping("/internal/bookings/{id}/confirm")
	void confirmBooking(@PathVariable("id") Long id);
}