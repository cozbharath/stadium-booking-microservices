package com.stadiumbooking.booking.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.stadiumbooking.booking.repository.EventSeatStockRepository;
import com.stadiumbooking.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

/**
 * Replaces EventSeatRepository.findByIdForUpdate from the monolith. Call it
 * inside a transaction: the row lock is held until that transaction ends, so
 * two people can never hold or book the same seat at the same time.
 */
@Component
@RequiredArgsConstructor
public class SeatStockLocker {

	private final EventSeatStockRepository stockRepository;

	@Transactional
	public void lock(Long eventSeatId, Long eventId) {
		stockRepository.insertIfAbsent(eventSeatId, eventId);
		stockRepository.findByIdForUpdate(eventSeatId)
				.orElseThrow(() -> new ResourceNotFoundException("Event seat not found with id: " + eventSeatId));
	}
}