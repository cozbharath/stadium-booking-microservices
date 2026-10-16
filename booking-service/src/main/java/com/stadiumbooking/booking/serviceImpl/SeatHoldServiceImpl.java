package com.stadiumbooking.booking.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stadiumbooking.booking.client.EventLookup;
import com.stadiumbooking.booking.dto.seathold.request.SeatHoldRequest;
import com.stadiumbooking.booking.dto.seathold.response.SeatHoldResponse;
import com.stadiumbooking.booking.entity.SeatHold;
import com.stadiumbooking.booking.enums.HoldStatus;
import com.stadiumbooking.booking.repository.BookingSeatRepository;
import com.stadiumbooking.booking.repository.SeatHoldRepository;
import com.stadiumbooking.booking.service.SeatHoldService;
import com.stadiumbooking.booking.util.BookingRules;
import com.stadiumbooking.booking.util.SeatStockLocker;
import com.stadiumbooking.common.contract.EventInfo;
import com.stadiumbooking.common.contract.EventSeatInfo;
import com.stadiumbooking.common.exception.ResourceNotFoundException;
import com.stadiumbooking.common.exception.SeatNotAvailableException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatHoldServiceImpl implements SeatHoldService {

	private final SeatHoldRepository seatHoldRepository;
	private final BookingSeatRepository bookingSeatRepository;
	private final EventLookup eventLookup;
	private final SeatStockLocker seatStockLocker;

	private static final long HOLD_MINUTES = 10;

	@Override
	@Transactional
	public SeatHoldResponse holdSeats(SeatHoldRequest request, Long userId) {

		if (request.getEventId() == null) {
			throw new IllegalArgumentException("Event ID is required");
		}

		if (request.getEventSeatIds() == null || request.getEventSeatIds().isEmpty()) {
			throw new IllegalArgumentException("At least one seat must be selected");
		}

		EventInfo event = eventLookup.getEvent(request.getEventId());

		BookingRules.assertEventBookable(event);

		// sorted so two people locking overlapping seats always lock in the same order
		// (no deadlocks)
		TreeSet<Long> eventSeatIds = new TreeSet<>(request.getEventSeatIds());
		Map<Long, EventSeatInfo> seats = eventLookup.getSeats(eventSeatIds);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiresAt = now.plusMinutes(HOLD_MINUTES);

		// the user re-selecting seats for the same event replaces their previous active
		// hold
		List<SeatHold> ownActiveHolds = seatHoldRepository.findByUserId(userId).stream()
				.filter(h -> h.getStatus() == HoldStatus.ACTIVE && h.getEventId().equals(request.getEventId()))
				.toList();

		for (SeatHold old : ownActiveHolds) {
			old.setStatus(HoldStatus.RELEASED);
		}

		if (!ownActiveHolds.isEmpty()) {
			seatHoldRepository.saveAll(ownActiveHolds);
			seatHoldRepository.flush();
		}

		String holdToken = UUID.randomUUID().toString();
		List<Long> heldSeatIds = new ArrayList<>();

		for (Long eventSeatId : eventSeatIds) {

			EventSeatInfo seat = seats.get(eventSeatId);

			if (!seat.eventId().equals(request.getEventId())) {
				throw new IllegalArgumentException("Event seat does not belong to the selected event");
			}

			// lock this seat until the transaction ends
			seatStockLocker.lock(eventSeatId, request.getEventId());

			boolean alreadyHeld = seatHoldRepository.existsByEventSeatIdAndStatusAndExpiresAtAfter(eventSeatId,
					HoldStatus.ACTIVE, now);

			boolean alreadyBooked = bookingSeatRepository.isSeatTaken(eventSeatId, BookingRules.SEAT_BLOCKING_STATUSES);

			if (alreadyHeld || alreadyBooked) {
				throw new SeatNotAvailableException("Seat " + eventSeatId + " is no longer available");
			}

			SeatHold hold = SeatHold.builder().eventSeatId(eventSeatId).eventId(request.getEventId()).userId(userId)
					.holdToken(holdToken).heldAt(now).expiresAt(expiresAt).status(HoldStatus.ACTIVE).build();

			seatHoldRepository.save(hold);
			heldSeatIds.add(eventSeatId);
		}

		return new SeatHoldResponse(holdToken, heldSeatIds, now, expiresAt, HoldStatus.ACTIVE);
	}

	@Override
	@Transactional
	public void releaseHold(String holdToken, Long userId) {

		List<SeatHold> holds = seatHoldRepository.findByHoldToken(holdToken);

		if (holds.isEmpty()) {
			throw new ResourceNotFoundException("Hold not found");
		}

		for (SeatHold hold : holds) {

			if (!hold.getUserId().equals(userId)) {
				throw new AccessDeniedException("You cannot release another user's hold");
			}

			if (hold.getStatus() == HoldStatus.ACTIVE) {
				hold.setStatus(HoldStatus.RELEASED);
			}
		}

		seatHoldRepository.saveAll(holds);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SeatHoldResponse> getUserHolds(Long userId) {

		List<SeatHold> holds = seatHoldRepository.findByUserId(userId);

		return holds.stream().collect(Collectors.groupingBy(SeatHold::getHoldToken)).values().stream().map(holdList -> {
			SeatHold firstHold = holdList.get(0);
			List<Long> eventSeatIds = holdList.stream().map(SeatHold::getEventSeatId).toList();
			return new SeatHoldResponse(firstHold.getHoldToken(), eventSeatIds, firstHold.getHeldAt(),
					firstHold.getExpiresAt(), firstHold.getStatus());
		}).toList();
	}

	@Override
	@Transactional
	public void expireHolds() {

		LocalDateTime now = LocalDateTime.now();

		List<SeatHold> expiredHolds = seatHoldRepository.findByStatusAndExpiresAtBefore(HoldStatus.ACTIVE, now);

		for (SeatHold hold : expiredHolds) {
			hold.setStatus(HoldStatus.EXPIRED);
		}

		if (!expiredHolds.isEmpty()) {
			seatHoldRepository.saveAll(expiredHolds);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> getUnavailableSeatIds(Long eventId, Long userId) {

		TreeSet<Long> ids = new TreeSet<>();

		ids.addAll(seatHoldRepository.findHeldByOthers(eventId, HoldStatus.ACTIVE, LocalDateTime.now(), userId));
		ids.addAll(bookingSeatRepository.findTakenEventSeatIds(eventId, BookingRules.SEAT_BLOCKING_STATUSES));

		return new ArrayList<>(ids);
	}
}