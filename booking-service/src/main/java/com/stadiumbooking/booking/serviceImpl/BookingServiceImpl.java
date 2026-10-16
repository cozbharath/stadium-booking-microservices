package com.stadiumbooking.booking.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stadiumbooking.booking.client.EventLookup;
import com.stadiumbooking.booking.client.TicketClient;
import com.stadiumbooking.booking.dto.booking.request.BookingRequest;
import com.stadiumbooking.booking.entity.Booking;
import com.stadiumbooking.booking.entity.BookingSeat;
import com.stadiumbooking.booking.entity.SeatHold;
import com.stadiumbooking.booking.enums.HoldStatus;
import com.stadiumbooking.booking.repository.BookingRepository;
import com.stadiumbooking.booking.repository.BookingSeatRepository;
import com.stadiumbooking.booking.repository.SeatHoldRepository;
import com.stadiumbooking.booking.service.BookingService;
import com.stadiumbooking.booking.util.BookingRules;
import com.stadiumbooking.booking.util.SeatStockLocker;
import com.stadiumbooking.common.contract.CreateTicketsRequest;
import com.stadiumbooking.common.contract.EventInfo;
import com.stadiumbooking.common.contract.EventSeatInfo;
import com.stadiumbooking.common.contract.InternalBookingResponse;
import com.stadiumbooking.common.contract.InternalSeatData;
import com.stadiumbooking.common.dto.BookingResponse;
import com.stadiumbooking.common.enums.BookingStatus;
import com.stadiumbooking.common.exception.BookingException;
import com.stadiumbooking.common.exception.ResourceNotFoundException;
import com.stadiumbooking.common.exception.SeatHoldExpiredException;
import com.stadiumbooking.common.exception.SeatNotAvailableException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

	private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

	private final BookingRepository bookingRepository;
	private final BookingSeatRepository bookingSeatRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final EventLookup eventLookup;
	private final SeatStockLocker seatStockLocker;
	private final TicketClient ticketClient;

	// ==================================================
	// CREATE BOOKING
	// ==================================================

	@Override
	@Transactional
	public BookingResponse createBooking(BookingRequest request, Long userId, String customerName,
			String customerEmail) {

		if (request == null) {
			throw new IllegalArgumentException("Booking request is required");
		}

		if (userId == null) {
			throw new IllegalArgumentException("User ID is required");
		}

		if (request.getEventId() == null) {
			throw new IllegalArgumentException("Event ID is required");
		}

		if (request.getEventSeatIds() == null || request.getEventSeatIds().isEmpty()) {
			throw new IllegalArgumentException("No seats selected");
		}

		if (request.getHoldToken() == null || request.getHoldToken().isBlank()) {
			throw new IllegalArgumentException("Hold token is required");
		}

		if (request.getEventSeatIds().contains(null)) {
			throw new IllegalArgumentException("Event seat ID cannot be null");
		}

		EventInfo event = eventLookup.getEvent(request.getEventId());

		BookingRules.assertEventBookable(event);

		// sorted lock order (see SeatHoldServiceImpl)
		TreeSet<Long> eventSeatIds = new TreeSet<>(request.getEventSeatIds());
		Map<Long, EventSeatInfo> seats = eventLookup.getSeats(eventSeatIds);

		List<SeatHold> holdsForToken = seatHoldRepository.findByHoldToken(request.getHoldToken());

		List<BookingSeat> bookingSeats = new ArrayList<>();
		List<SeatHold> holdsToConvert = new ArrayList<>();

		BigDecimal totalAmount = BigDecimal.ZERO;
		LocalDateTime now = LocalDateTime.now();

		for (Long eventSeatId : eventSeatIds) {

			EventSeatInfo seat = seats.get(eventSeatId);

			if (!seat.eventId().equals(event.id())) {
				throw new IllegalArgumentException("Event seat does not belong to this event");
			}

			seatStockLocker.lock(eventSeatId, event.id());

			SeatHold hold = holdsForToken.stream()
					.filter(h -> h.getEventSeatId().equals(eventSeatId) && h.getUserId().equals(userId)
							&& h.getStatus() == HoldStatus.ACTIVE)
					.findFirst().orElseThrow(() -> new SeatHoldExpiredException("Seat hold is invalid or expired"));

			if (hold.getExpiresAt().isBefore(now)) {
				throw new SeatHoldExpiredException("Seat hold has expired");
			}

			if (bookingSeatRepository.isSeatTaken(eventSeatId, BookingRules.SEAT_BLOCKING_STATUSES)) {
				throw new SeatNotAvailableException("Seat is already booked: " + eventSeatId);
			}

			// price and seat label come from event-service now and are stored on the
			// booking
			bookingSeats.add(BookingSeat.builder().eventSeatId(eventSeatId).price(seat.price())
					.standName(seat.standName()).rowNumber(seat.rowNumber()).seatNumber(seat.seatNumber()).build());

			totalAmount = totalAmount.add(seat.price());
			holdsToConvert.add(hold);
		}

		Booking booking = Booking.builder().userId(userId).eventId(event.id()).customerName(customerName)
				.customerEmail(customerEmail).eventName(event.name()).eventDate(event.eventDate())
				.startTime(event.startTime()).stadiumName(event.stadiumName()).bookingDate(now).totalAmount(totalAmount)
				.status(BookingStatus.PENDING).build();

		Booking savedBooking = bookingRepository.save(booking);

		for (BookingSeat bookingSeat : bookingSeats) {
			bookingSeat.setBooking(savedBooking);
		}

		bookingSeatRepository.saveAll(bookingSeats);

		savedBooking.setBookingSeats(bookingSeats);

		for (SeatHold hold : holdsToConvert) {
			hold.setStatus(HoldStatus.CONVERTED);
		}

		seatHoldRepository.saveAll(holdsToConvert);

		return mapToResponse(savedBooking);
	}

	// ==================================================
	// READ
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public BookingResponse getBookingById(Long id) {
		return mapToResponse(findBooking(id));
	}

	@Override
	@Transactional(readOnly = true)
	public BookingResponse getMyBookingById(Long bookingId, Long userId) {

		Booking booking = findBooking(bookingId);

		if (!booking.getUserId().equals(userId)) {
			throw new AccessDeniedException("You cannot access this booking");
		}

		return mapToResponse(booking);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookingResponse> getMyBookings(Long userId) {
		return bookingRepository.findByUserId(userId).stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookingResponse> getBookingsByUserName(String userName) {
		return bookingRepository.findByCustomerName(userName).stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookingResponse> getBookingsByEvent(Long eventId) {
		return bookingRepository.findByEventId(eventId).stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookingResponse> getAllBookings() {
		return bookingRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	// ==================================================
	// CANCEL / EXPIRE
	// ==================================================

	@Override
	@Transactional
	public void cancelBooking(Long id, Long userId) {

		Booking booking = findBooking(id);

		if (!booking.getUserId().equals(userId)) {
			throw new AccessDeniedException("You cannot cancel this booking");
		}

		if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.EXPIRED) {
			throw new BookingException("This booking is already " + booking.getStatus().name().toLowerCase());
		}

		LocalDate eventDate = booking.getEventDate();

		if (eventDate != null && eventDate.isBefore(LocalDate.now())) {
			throw new BookingException("A booking for an event that has already taken place cannot be cancelled");
		}

		boolean wasConfirmed = booking.getStatus() == BookingStatus.CONFIRMED;

		booking.setStatus(BookingStatus.CANCELLED);
		bookingRepository.save(booking);

		if (wasConfirmed) {
			// ticket-service marks this booking's tickets CANCELLED (if this call fails,
			// the cancel is rolled back)
			ticketClient.cancelTickets(booking.getId());
			log.warn("Confirmed booking {} cancelled; payment refund must be processed manually", booking.getId());
		}
	}

	@Override
	@Transactional
	public void expirePendingBookings() {

		LocalDateTime cutoff = LocalDateTime.now().minusMinutes(BookingRules.PENDING_BOOKING_MINUTES);

		List<Booking> stale = bookingRepository.findByStatusAndBookingDateBefore(BookingStatus.PENDING, cutoff);

		for (Booking booking : stale) {
			booking.setStatus(BookingStatus.EXPIRED);
		}

		if (!stale.isEmpty()) {
			bookingRepository.saveAll(stale);
			log.info("Expired {} unpaid pending booking(s)", stale.size());
		}
	}

	// ==================================================
	// INTERNAL (payment-service)
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public InternalBookingResponse getInternalBooking(Long id) {
		Booking b = findBooking(id);
		return new InternalBookingResponse(b.getId(), b.getUserId(), b.getEventId(), b.getStatus(), b.getTotalAmount(),
				b.getRazorpayOrderId(), b.getCustomerName(), b.getCustomerEmail(), b.getEventName());
	}

	@Override
	@Transactional
	public void updateRazorpayOrder(Long id, String razorpayOrderId) {
		Booking booking = findBooking(id);
		booking.setRazorpayOrderId(razorpayOrderId);
		bookingRepository.save(booking);
	}

	@Override
	@Transactional
	public void confirmBooking(Long id) {

		Booking booking = findBooking(id);

		// payment-service may retry after a timeout, so confirming twice is fine
		if (booking.getStatus() == BookingStatus.CONFIRMED) {
			return;
		}

		if (booking.getStatus() != BookingStatus.PENDING) {
			throw new BookingException("Only a pending booking can be confirmed");
		}

		booking.setStatus(BookingStatus.CONFIRMED);
		bookingRepository.save(booking);

		List<InternalSeatData> seatData = booking.getBookingSeats().stream().map(bs -> new InternalSeatData(bs.getId(),
				bs.getStandName(), bs.getRowNumber(), bs.getSeatNumber(), bs.getPrice())).toList();

		// if ticket creation fails the exception rolls the confirmation back as well
		ticketClient.createTickets(new CreateTicketsRequest(booking.getId(), booking.getUserId(),
				booking.getCustomerName(), booking.getEventName(), booking.getEventDate(), booking.getStartTime(),
				booking.getStadiumName(), booking.getTotalAmount(), seatData));
	}

	// ==================================================
	// HELPERS
	// ==================================================

	private Booking findBooking(Long id) {
		return bookingRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
	}

	private BookingResponse mapToResponse(Booking booking) {

		List<String> seats = new ArrayList<>();

		if (booking.getBookingSeats() != null) {
			for (BookingSeat bookingSeat : booking.getBookingSeats()) {
				seats.add(bookingSeat.getStandName() + " - Row " + bookingSeat.getRowNumber() + " - Seat "
						+ bookingSeat.getSeatNumber());
			}
		}

		return new BookingResponse(booking.getId(), booking.getEventName(), booking.getEventDate(),
				booking.getStartTime(), booking.getStadiumName(), seats, booking.getTotalAmount(), booking.getStatus(),
				booking.getBookingDate(), booking.getCustomerName(), booking.getCustomerEmail());
	}
}