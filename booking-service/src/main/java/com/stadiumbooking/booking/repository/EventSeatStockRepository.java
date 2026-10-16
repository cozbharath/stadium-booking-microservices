package com.stadiumbooking.booking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stadiumbooking.booking.entity.EventSeatStock;

import jakarta.persistence.LockModeType;

public interface EventSeatStockRepository extends JpaRepository<EventSeatStock, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM EventSeatStock s WHERE s.eventSeatId = :id")
	Optional<EventSeatStock> findByIdForUpdate(@Param("id") Long id);

	// MySQL: creates the row the first time a seat is seen, silently does nothing
	// if it already exists
	@Modifying
	@Query(value = "INSERT IGNORE INTO event_seat_stock (event_seat_id, event_id) VALUES (:id, :eventId)", nativeQuery = true)
	void insertIfAbsent(@Param("id") Long id, @Param("eventId") Long eventId);
}