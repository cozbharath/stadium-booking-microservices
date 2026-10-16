package com.stadiumbooking.booking.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "booking_seats", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "booking_id", "event_seat_id" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSeat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "booking_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Booking booking;

	// was: @ManyToOne EventSeat eventSeat
	@Column(name = "event_seat_id", nullable = false)
	private Long eventSeatId;

	private BigDecimal price;

	// snapshot of the seat location (used in booking and ticket responses)
	private String standName;

	@Column(name = "row_num")
	private String rowNumber;

	private Integer seatNumber;
}