package com.stadiumbooking.event.dto.event.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.stadiumbooking.common.enums.EventStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

	private Long id;

	private String name;

	private LocalDate eventDate;

	private LocalTime startTime;

	private LocalTime endTime;

	private EventStatus status;

	private Long stadiumId;

	private String stadiumName;

	private String location;

	private String imageUrl;
}