package com.stadiumbooking.event.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stadiumbooking.common.contract.SeatInfo;
import com.stadiumbooking.common.exception.ResourceNotFoundException;
import com.stadiumbooking.event.client.VenueClient;
import com.stadiumbooking.event.dto.eventseat.request.BulkEventSeatRequest;
import com.stadiumbooking.event.dto.eventseat.request.EventSeatRequest;
import com.stadiumbooking.event.dto.eventseat.response.EventSeatResponse;
import com.stadiumbooking.event.entity.Event;
import com.stadiumbooking.event.entity.EventSeat;
import com.stadiumbooking.event.repository.EventRepository;
import com.stadiumbooking.event.repository.EventSeatRepository;
import com.stadiumbooking.event.service.EventSeatService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventSeatServiceImpl implements EventSeatService {

	private final EventSeatRepository eventSeatRepository;
	private final EventRepository eventRepository;
	private final VenueClient venueClient;

	private SeatInfo findSeat(Long seatId) {
		if (seatId == null) {
			throw new IllegalArgumentException("Seat ID is required");
		}
		try {
			return venueClient.getSeat(seatId);
		} catch (FeignException.NotFound e) {
			throw new ResourceNotFoundException("Seat not found with id: " + seatId);
		}
	}

	private Event findEvent(Long eventId) {
		return eventRepository.findById(eventId)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
	}

	private void applySeat(EventSeat eventSeat, SeatInfo seat) {
		eventSeat.setSeatId(seat.id());
		eventSeat.setStandName(seat.standName());
		eventSeat.setRowNumber(seat.rowNumber());
		eventSeat.setSeatNumber(seat.seatNumber());
	}

	@Override
	@Transactional
	public EventSeatResponse createEventSeat(EventSeatRequest request) {

		Event event = findEvent(request.getEventId());
		SeatInfo seat = findSeat(request.getSeatId());

		if (eventSeatRepository.existsByEventIdAndSeatId(request.getEventId(), request.getSeatId())) {
			throw new IllegalArgumentException("Seat is already configured for this event");
		}

		EventSeat eventSeat = new EventSeat();
		eventSeat.setEvent(event);
		applySeat(eventSeat, seat);
		eventSeat.setPrice(request.getPrice());

		return mapToResponse(eventSeatRepository.save(eventSeat));
	}

	@Override
	@Transactional(readOnly = true)
	public EventSeatResponse getEventSeatById(Long id) {
		return mapToResponse(eventSeatRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event seat not found with id: " + id)));
	}

	@Override
	@Transactional(readOnly = true)
	public List<EventSeatResponse> getAllEventSeats() {
		return eventSeatRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<EventSeatResponse> getEventSeatsByEvent(Long eventId) {
		return eventSeatRepository.findByEventId(eventId).stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional
	public EventSeatResponse updateEventSeat(Long id, EventSeatRequest request) {

		EventSeat eventSeat = eventSeatRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event seat not found with id: " + id));

		Event event = findEvent(request.getEventId());
		SeatInfo seat = findSeat(request.getSeatId());

		eventSeat.setEvent(event);
		applySeat(eventSeat, seat);
		eventSeat.setPrice(request.getPrice());

		return mapToResponse(eventSeatRepository.save(eventSeat));
	}

	@Override
	@Transactional
	public void deleteEventSeat(Long id) {
		EventSeat eventSeat = eventSeatRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event seat not found with id: " + id));
		eventSeatRepository.delete(eventSeat);
	}

	private EventSeatResponse mapToResponse(EventSeat eventSeat) {
		return new EventSeatResponse(eventSeat.getId(), eventSeat.getEvent().getId(), eventSeat.getSeatId(),
				eventSeat.getStandName(), eventSeat.getRowNumber(), eventSeat.getSeatNumber(), eventSeat.getPrice());
	}

	@Override
	@Transactional
	public List<EventSeatResponse> createEventSeatsBulk(BulkEventSeatRequest request) {

		Event event = findEvent(request.getEventId());

		if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
			throw new IllegalArgumentException("At least one seat must be selected");
		}

		List<EventSeat> existing = eventSeatRepository.findByEventIdAndSeatIdIn(request.getEventId(),
				request.getSeatIds());

		java.util.Map<Long, EventSeat> existingBySeatId = existing.stream()
				.collect(java.util.stream.Collectors.toMap(EventSeat::getSeatId, es -> es));

		List<EventSeat> toSave = new java.util.ArrayList<>();

		for (Long seatId : request.getSeatIds()) {

			EventSeat eventSeat = existingBySeatId.get(seatId);

			if (eventSeat == null) {
				SeatInfo seat = findSeat(seatId);
				eventSeat = new EventSeat();
				eventSeat.setEvent(event);
				applySeat(eventSeat, seat);
			}

			eventSeat.setPrice(request.getPrice());
			toSave.add(eventSeat);
		}

		return eventSeatRepository.saveAll(toSave).stream().map(this::mapToResponse).toList();
	}
}