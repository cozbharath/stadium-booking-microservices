package com.stadiumbooking.event.entity;

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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_seats", uniqueConstraints = { @UniqueConstraint(columnNames = { "event_id", "seat_id" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSeat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	// was: @ManyToOne Seat seat. The seat lives in venue-service now.
	@Column(name = "seat_id", nullable = false)
	private Long seatId;

	private String standName;

	@Column(name = "row_num")
	private String rowNumber;

	private Integer seatNumber;

	@Column(nullable = false)
	private BigDecimal price;
}