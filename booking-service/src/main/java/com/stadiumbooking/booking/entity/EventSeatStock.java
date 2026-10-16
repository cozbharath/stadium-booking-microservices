package com.stadiumbooking.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Local row per event seat. It exists only so booking-service has something to
 * lock (SELECT ... FOR UPDATE) while holding or booking, the same job
 * EventSeat.findByIdForUpdate did in the monolith.
 */
@Entity
@Table(name = "event_seat_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSeatStock {

	@Id
	@Column(name = "event_seat_id")
	private Long eventSeatId;

	@Column(name = "event_id", nullable = false)
	private Long eventId;
}