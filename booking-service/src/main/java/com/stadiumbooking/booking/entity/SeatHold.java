package com.stadiumbooking.booking.entity;

import java.time.LocalDateTime;

import com.stadiumbooking.booking.enums.HoldStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seat_holds")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatHold {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// was: @ManyToOne EventSeat eventSeat (EventSeat lives in event-service now)
	@Column(name = "event_seat_id", nullable = false)
	private Long eventSeatId;

	@Column(name = "event_id", nullable = false)
	private Long eventId;

	// was: @ManyToOne User user (User lives in auth-service now)
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(nullable = false)
	private String holdToken;

	private LocalDateTime heldAt;

	private LocalDateTime expiresAt;

	@Enumerated(EnumType.STRING)
	private HoldStatus status;
}