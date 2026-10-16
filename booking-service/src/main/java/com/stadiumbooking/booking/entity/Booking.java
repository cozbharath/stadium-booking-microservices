package com.stadiumbooking.booking.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.stadiumbooking.common.enums.BookingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// was: @ManyToOne User user
	@Column(name = "user_id", nullable = false)
	private Long userId;

	// was: @ManyToOne Event event
	@Column(name = "event_id", nullable = false)
	private Long eventId;

	// snapshots copied when the booking is created, so responses need no other
	// service
	private String customerName;

	private String customerEmail;

	private String eventName;

	private LocalDate eventDate;

	private LocalTime startTime;

	private String stadiumName;

	private LocalDateTime bookingDate;

	private BigDecimal totalAmount;

	@Enumerated(EnumType.STRING)
	private BookingStatus status;

	@Column(name = "razorpay_order_id")
	private String razorpayOrderId;

	// payments and tickets are no longer navigable from here (they live in other
	// services)
	@OneToMany(mappedBy = "booking")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<BookingSeat> bookingSeats;
}