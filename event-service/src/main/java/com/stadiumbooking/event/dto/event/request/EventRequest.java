package com.stadiumbooking.event.dto.event.request;

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
public class EventRequest {

	private Long stadiumId;

	private String name;

	private LocalDate eventDate;

	private LocalTime startTime;

	private LocalTime endTime;

	private String imageUrl;

	private EventStatus status;
}