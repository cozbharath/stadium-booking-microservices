package com.stadiumbooking.booking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stadiumbooking.booking.entity.Booking;
import com.stadiumbooking.common.enums.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	List<Booking> findByUserId(Long userId);

	List<Booking> findByEventId(Long eventId);

	List<Booking> findByStatusAndBookingDateBefore(BookingStatus status, LocalDateTime time);

	// was a JPQL join on b.user.name; the name is a snapshot on the booking now
	List<Booking> findByCustomerName(String customerName);
}