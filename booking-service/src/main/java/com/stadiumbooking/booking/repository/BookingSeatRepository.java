package com.stadiumbooking.booking.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stadiumbooking.booking.entity.BookingSeat;
import com.stadiumbooking.common.enums.BookingStatus;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

	List<BookingSeat> findByBookingId(Long bookingId);

	@Query("SELECT COUNT(bs) > 0 FROM BookingSeat bs WHERE bs.eventSeatId = :eventSeatId AND bs.booking.status IN :statuses")
	boolean isSeatTaken(@Param("eventSeatId") Long eventSeatId, @Param("statuses") Collection<BookingStatus> statuses);

	@Query("SELECT bs.eventSeatId FROM BookingSeat bs WHERE bs.booking.eventId = :eventId AND bs.booking.status IN :statuses")
	List<Long> findTakenEventSeatIds(@Param("eventId") Long eventId,
			@Param("statuses") Collection<BookingStatus> statuses);
}