package com.stadiumbooking.event.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stadiumbooking.common.enums.EventStatus;
import com.stadiumbooking.event.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

	List<Event> findByStatus(EventStatus status);

	List<Event> findByStadiumId(Long stadiumId);

	List<Event> findByStatusAndEventDateBefore(EventStatus status, LocalDate date);
}