package com.stadiumbooking.admin.serviceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.stadiumbooking.admin.client.BookingClient;
import com.stadiumbooking.admin.dto.report.response.EventBookingSummaryResponse;
import com.stadiumbooking.admin.dto.report.response.EventRevenueResponse;
import com.stadiumbooking.admin.dto.report.response.RevenueReportResponse;
import com.stadiumbooking.admin.service.AdminReportService;
import com.stadiumbooking.common.dto.BookingResponse;
import com.stadiumbooking.common.enums.BookingStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

	private final BookingClient bookingClient;

	@Override
	public List<BookingResponse> getAllBookings() {
		return bookingClient.getAllBookings();
	}

	@Override
	public List<BookingResponse> getBookingsByUserName(String userName) {
		return bookingClient.getBookingsByUserName(userName);
	}

	@Override
	public List<BookingResponse> getBookingsByEvent(Long eventId) {
		return bookingClient.getBookingsByEvent(eventId);
	}

	@Override
	public RevenueReportResponse getRevenueReport() {

		List<BookingResponse> all = bookingClient.getAllBookings();

		BigDecimal total = BigDecimal.ZERO;
		Map<String, BigDecimal> revenueByEvent = new TreeMap<>();
		Map<String, Long> countByEvent = new TreeMap<>();

		for (BookingResponse b : all) {
			if (b.getStatus() == BookingStatus.CONFIRMED && b.getTotalAmount() != null) {
				total = total.add(b.getTotalAmount());
				revenueByEvent.merge(b.getEventName(), b.getTotalAmount(), BigDecimal::add);
				countByEvent.merge(b.getEventName(), 1L, Long::sum);
			}
		}

		List<EventRevenueResponse> perEvent = revenueByEvent.entrySet().stream()
				.map(e -> new EventRevenueResponse(e.getKey(), countByEvent.get(e.getKey()), e.getValue())).toList();

		return new RevenueReportResponse(total, count(all, BookingStatus.CONFIRMED), count(all, BookingStatus.PENDING),
				count(all, BookingStatus.CANCELLED), count(all, BookingStatus.EXPIRED), perEvent);
	}

	@Override
	public EventBookingSummaryResponse getEventSummary(Long eventId) {

		List<BookingResponse> bookings = bookingClient.getBookingsByEvent(eventId);

		String eventName = bookings.isEmpty() ? null : bookings.get(0).getEventName();

		BigDecimal revenue = BigDecimal.ZERO;
		long seatsSold = 0;

		for (BookingResponse b : bookings) {
			if (b.getStatus() == BookingStatus.CONFIRMED) {
				if (b.getTotalAmount() != null) {
					revenue = revenue.add(b.getTotalAmount());
				}
				seatsSold += b.getSeats() == null ? 0 : b.getSeats().size();
			}
		}

		return new EventBookingSummaryResponse(eventId, eventName, bookings.size(),
				count(bookings, BookingStatus.CONFIRMED), seatsSold, revenue);
	}

	private long count(List<BookingResponse> bookings, BookingStatus status) {
		return bookings.stream().filter(b -> b.getStatus() == status).count();
	}
}