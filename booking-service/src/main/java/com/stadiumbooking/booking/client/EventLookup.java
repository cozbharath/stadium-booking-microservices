package com.stadiumbooking.booking.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.stadiumbooking.common.contract.EventInfo;
import com.stadiumbooking.common.contract.EventSeatInfo;
import com.stadiumbooking.common.exception.ResourceNotFoundException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

/**
 * Thin wrapper over EventClient that turns "404 from event-service" into
 * ResourceNotFoundException.
 */
@Component
@RequiredArgsConstructor
public class EventLookup {

	private final EventClient eventClient;

	public EventInfo getEvent(Long eventId) {
		try {
			return eventClient.getEvent(eventId);
		} catch (FeignException.NotFound e) {
			throw new ResourceNotFoundException("Event not found with id: " + eventId);
		}
	}

	public Map<Long, EventSeatInfo> getSeats(Collection<Long> eventSeatIds) {
		List<EventSeatInfo> found = eventClient.getEventSeats(List.copyOf(eventSeatIds));
		Map<Long, EventSeatInfo> byId = new HashMap<>();
		for (EventSeatInfo info : found) {
			byId.put(info.id(), info);
		}
		for (Long id : eventSeatIds) {
			if (!byId.containsKey(id)) {
				throw new ResourceNotFoundException("Event seat not found with id: " + id);
			}
		}
		return byId;
	}
}