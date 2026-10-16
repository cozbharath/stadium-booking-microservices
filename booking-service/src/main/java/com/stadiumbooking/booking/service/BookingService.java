package com.stadiumbooking.booking.service;

import java.util.List;

import com.stadiumbooking.booking.dto.booking.request.BookingRequest;
import com.stadiumbooking.common.contract.InternalBookingResponse;
import com.stadiumbooking.common.dto.BookingResponse;

public interface BookingService {

	// customerName and customerEmail come from the logged-in user and are stored on
	// the booking
	BookingResponse createBooking(BookingRequest request, Long userId, String customerName, String customerEmail);

	BookingResponse getBookingById(Long id);

	BookingResponse getMyBookingById(Long bookingId, Long userId);

	List<BookingResponse> getMyBookings(Long userId);

	List<BookingResponse> getBookingsByUserName(String userName);

	List<BookingResponse> getBookingsByEvent(Long eventId);

	void cancelBooking(Long id, Long userId);

	void expirePendingBookings();

	List<BookingResponse> getAllBookings();

	// ---- used by payment-service (through /internal/**) ----
	InternalBookingResponse getInternalBooking(Long id);

	void updateRazorpayOrder(Long id, String razorpayOrderId);

	void confirmBooking(Long id);
}