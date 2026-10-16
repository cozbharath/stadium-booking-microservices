package com.stadiumbooking.event.service;

import java.util.List;

import com.stadiumbooking.event.dto.event.request.EventRequest;
import com.stadiumbooking.event.dto.event.response.EventResponse;

public interface EventService {

	EventResponse createEvent(EventRequest request);

	EventResponse getEventById(Long id);

	List<EventResponse> getAllEvents();

	List<EventResponse> getUpcomingEvents();

	List<EventResponse> getEventsByStadium(Long stadiumId);

	EventResponse updateEvent(Long id, EventRequest request);

	void deleteEvent(Long id);

	void completePastEvents();
}