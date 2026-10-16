package com.stadiumbooking.booking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stadiumbooking.booking.entity.SeatHold;
import com.stadiumbooking.booking.enums.HoldStatus;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {

	List<SeatHold> findByStatusAndExpiresAtBefore(HoldStatus status, LocalDateTime time);

	List<SeatHold> findByUserId(Long userId);

	List<SeatHold> findByHoldToken(String holdToken);

	boolean existsByEventSeatIdAndStatusAndExpiresAtAfter(Long eventSeatId, HoldStatus status, LocalDateTime now);

	// active holds on this event by OTHER users (your own hold stays selectable)
	@Query("SELECT h.eventSeatId FROM SeatHold h WHERE h.eventId = :eventId AND h.status = :status "
			+ "AND h.expiresAt > :now AND h.userId <> :userId")
	List<Long> findHeldByOthers(@Param("eventId") Long eventId, @Param("status") HoldStatus status,
			@Param("now") LocalDateTime now, @Param("userId") Long userId);
}