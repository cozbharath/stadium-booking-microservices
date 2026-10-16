package com.stadiumbooking.event.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.stadiumbooking.common.enums.EventStatus;

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
@Table(name = "Events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private LocalDate eventDate;

	private LocalTime startTime;

	private LocalTime endTime;

	private String imageUrl;

	@Enumerated(EnumType.STRING)
	private EventStatus status;

	// was: @ManyToOne Stadium stadium. The stadium lives in venue-service now,
	// so we keep its id plus the name/location needed to show the event.
	@Column(name = "stadium_id", nullable = false)
	private Long stadiumId;

	private String stadiumName;

	private String stadiumLocation;
}