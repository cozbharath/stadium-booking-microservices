package com.stadiumbooking.event.serviceImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stadiumbooking.common.contract.StadiumInfo;
import com.stadiumbooking.common.enums.EventStatus;
import com.stadiumbooking.common.exception.ResourceNotFoundException;
import com.stadiumbooking.event.client.VenueClient;
import com.stadiumbooking.event.dto.event.request.EventRequest;
import com.stadiumbooking.event.dto.event.response.EventResponse;
import com.stadiumbooking.event.entity.Event;
import com.stadiumbooking.event.repository.EventRepository;
import com.stadiumbooking.event.service.EventService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;
	private final VenueClient venueClient;

	// the stadium now lives in venue-service
	private StadiumInfo findStadium(Long stadiumId) {
		if (stadiumId == null) {
			throw new IllegalArgumentException("Stadium ID is required");
		}
		try {
			return venueClient.getStadium(stadiumId);
		} catch (FeignException.NotFound e) {
			throw new ResourceNotFoundException("Stadium not found with id: " + stadiumId);
		}
	}

	@Override
	@Transactional
	public EventResponse createEvent(EventRequest request) {

		StadiumInfo stadium = findStadium(request.getStadiumId());

		Event event = new Event();
		event.setName(request.getName());
		event.setEventDate(request.getEventDate());
		event.setStartTime(request.getStartTime());
		event.setEndTime(request.getEndTime());
		event.setImageUrl(request.getImageUrl());
		event.setStatus(EventStatus.UPCOMING);
		event.setStadiumId(stadium.id());
		event.setStadiumName(stadium.name());
		event.setStadiumLocation(stadium.location());

		return mapToResponse(eventRepository.save(event));
	}

	@Override
	@Transactional(readOnly = true)
	public EventResponse getEventById(Long id) {
		return mapToResponse(eventRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id)));
	}

	@Override
	@Transactional(readOnly = true)
	public List<EventResponse> getAllEvents() {
		return eventRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<EventResponse> getUpcomingEvents() {
		return eventRepository.findByStatus(EventStatus.UPCOMING).stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<EventResponse> getEventsByStadium(Long stadiumId) {
		findStadium(stadiumId);
		return eventRepository.findByStadiumId(stadiumId).stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional
	public EventResponse updateEvent(Long id, EventRequest request) {

		Event event = eventRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

		StadiumInfo stadium = findStadium(request.getStadiumId());

		event.setName(request.getName());
		event.setEventDate(request.getEventDate());
		event.setStartTime(request.getStartTime());
		event.setEndTime(request.getEndTime());
		event.setImageUrl(request.getImageUrl());
		event.setStadiumId(stadium.id());
		event.setStadiumName(stadium.name());
		event.setStadiumLocation(stadium.location());

		// status only changes when the admin sends one
		if (request.getStatus() != null) {
			event.setStatus(request.getStatus());
		}

		return mapToResponse(eventRepository.save(event));
	}

	@Override
	@Transactional
	public void deleteEvent(Long id) {
		Event event = eventRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
		eventRepository.delete(event);
	}

	@Override
	@Transactional
	public void completePastEvents() {

		List<Event> pastUpcoming = eventRepository.findByStatusAndEventDateBefore(EventStatus.UPCOMING,
				LocalDate.now());

		for (Event event : pastUpcoming) {
			event.setStatus(EventStatus.COMPLETED);
		}

		if (!pastUpcoming.isEmpty()) {
			eventRepository.saveAll(pastUpcoming);
		}
	}

	private EventResponse mapToResponse(Event event) {
		return new EventResponse(event.getId(), event.getName(), event.getEventDate(), event.getStartTime(),
				event.getEndTime(), event.getStatus(), event.getStadiumId(), event.getStadiumName(),
				event.getStadiumLocation(), event.getImageUrl());
	}
}