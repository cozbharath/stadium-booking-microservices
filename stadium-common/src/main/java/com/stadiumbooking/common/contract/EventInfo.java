package com.stadiumbooking.common.contract;

import java.time.LocalDate;
import java.time.LocalTime;

import com.stadiumbooking.common.enums.EventStatus;

public record EventInfo(Long id, String name, LocalDate eventDate, LocalTime startTime, LocalTime endTime,
		EventStatus status, Long stadiumId, String stadiumName, String location, String imageUrl) {
}
