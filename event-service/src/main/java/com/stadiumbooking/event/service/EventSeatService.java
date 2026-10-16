package com.stadiumbooking.event.service;

import java.util.List;

import com.stadiumbooking.event.dto.eventseat.request.BulkEventSeatRequest;
import com.stadiumbooking.event.dto.eventseat.request.EventSeatRequest;
import com.stadiumbooking.event.dto.eventseat.response.EventSeatResponse;

public interface EventSeatService {

	EventSeatResponse createEventSeat(EventSeatRequest request);

	EventSeatResponse getEventSeatById(Long id);

	List<EventSeatResponse> getAllEventSeats();

	List<EventSeatResponse> getEventSeatsByEvent(Long eventId);

	EventSeatResponse updateEventSeat(Long id, EventSeatRequest request);

	void deleteEventSeat(Long id);

	List<EventSeatResponse> createEventSeatsBulk(BulkEventSeatRequest request);
}