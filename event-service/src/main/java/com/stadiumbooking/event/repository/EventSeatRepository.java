package com.stadiumbooking.event.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stadiumbooking.event.entity.EventSeat;

public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {

	List<EventSeat> findByEventId(Long eventId);

	Optional<EventSeat> findByEventIdAndSeatId(Long eventId, Long seatId);

	boolean existsByEventIdAndSeatId(Long eventId, Long seatId);

	List<EventSeat> findByEventIdAndSeatIdIn(Long eventId, List<Long> seatIds);
}