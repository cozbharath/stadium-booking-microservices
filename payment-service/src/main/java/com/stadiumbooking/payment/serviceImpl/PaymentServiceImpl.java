package com.stadiumbooking.payment.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.stadiumbooking.common.contract.InternalBookingResponse;
import com.stadiumbooking.common.contract.RazorpayOrderUpdate;
import com.stadiumbooking.common.enums.BookingStatus;
import com.stadiumbooking.common.exception.PaymentException;
import com.stadiumbooking.common.exception.ResourceNotFoundException;
import com.stadiumbooking.payment.client.BookingClient;
import com.stadiumbooking.payment.dto.payment.request.CreateOrderRequest;
import com.stadiumbooking.payment.dto.payment.request.VerifyPaymentRequest;
import com.stadiumbooking.payment.dto.payment.response.PaymentResponse;
import com.stadiumbooking.payment.dto.payment.response.RazorpayOrderResponse;
import com.stadiumbooking.payment.entity.Payment;
import com.stadiumbooking.payment.enums.PaymentStatus;
import com.stadiumbooking.payment.repository.PaymentRepository;
import com.stadiumbooking.payment.service.PaymentService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final BookingClient bookingClient;
	private final RazorpayClient razorpayClient;

	@Value("${razorpay.key.id}")
	private String razorpayKeyId;

	@Value("${razorpay.key.secret}")
	private String razorpayKeySecret;

	// the booking now lives in booking-service
	private InternalBookingResponse findBooking(Long bookingId) {
		try {
			return bookingClient.getBooking(bookingId);
		} catch (FeignException.NotFound e) {
			throw new ResourceNotFoundException("Booking not found with id: " + bookingId);
		}
	}

	// ==================================================
	// CREATE RAZORPAY ORDER
	// ==================================================

	@Override
	public RazorpayOrderResponse createRazorpayOrder(CreateOrderRequest request, Long userId) {

		if (request == null || request.getBookingId() == null) {
			throw new IllegalArgumentException("Booking ID is required");
		}

		InternalBookingResponse booking = findBooking(request.getBookingId());

		if (!booking.userId().equals(userId)) {
			throw new AccessDeniedException("You cannot create a payment for another user's booking");
		}

		if (booking.status() != BookingStatus.PENDING) {
			throw new IllegalArgumentException("Payment can be created only for a pending booking");
		}

		BigDecimal amount = booking.totalAmount();

		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Booking amount must be greater than zero");
		}

		try {

			long amountInPaise = amount.multiply(BigDecimal.valueOf(100)).longValueExact();

			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", amountInPaise);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "BOOKING_" + booking.bookingId());

			Order order = razorpayClient.orders.create(orderRequest);

			String orderId = order.get("id");

			// the order id is stored on the booking (booking-service) so verify can match
			// it later
			bookingClient.setRazorpayOrder(booking.bookingId(), new RazorpayOrderUpdate(orderId));

			return new RazorpayOrderResponse(orderId, booking.bookingId(), amount, "INR", razorpayKeyId);

		} catch (Exception e) {
			throw new PaymentException("Failed to create Razorpay order: " + e.getMessage());
		}
	}

	// ==================================================
	// VERIFY PAYMENT
	// ==================================================

	@Override
	@Transactional
	public PaymentResponse verifyRazorpayPayment(VerifyPaymentRequest request, Long userId) {

		if (request == null) {
			throw new IllegalArgumentException("Payment verification request is required");
		}

		if (request.getBookingId() == null) {
			throw new IllegalArgumentException("Booking ID is required");
		}

		if (request.getRazorpayOrderId() == null || request.getRazorpayOrderId().isBlank()) {
			throw new IllegalArgumentException("Razorpay order ID is required");
		}

		if (request.getRazorpayPaymentId() == null || request.getRazorpayPaymentId().isBlank()) {
			throw new IllegalArgumentException("Razorpay payment ID is required");
		}

		if (request.getRazorpaySignature() == null || request.getRazorpaySignature().isBlank()) {
			throw new IllegalArgumentException("Razorpay signature is required");
		}

		InternalBookingResponse booking = findBooking(request.getBookingId());

		if (!booking.userId().equals(userId)) {
			throw new AccessDeniedException("You cannot verify a payment for another user's booking");
		}

		Payment existingPayment = paymentRepository.findByTransactionId(request.getRazorpayPaymentId()).orElse(null);

		if (existingPayment != null) {

			if (!existingPayment.getBookingId().equals(booking.bookingId())) {
				throw new PaymentException("This payment does not belong to the given booking");
			}

			// repeat call (e.g. the browser retried): make sure the booking really is
			// confirmed. Safe to call twice.
			bookingClient.confirmBooking(booking.bookingId());

			return mapToResponse(existingPayment);
		}

		// CONFIRMED is allowed here only to recover from "booking confirmed but the
		// payment row was not saved"
		if (booking.status() != BookingStatus.PENDING && booking.status() != BookingStatus.CONFIRMED) {
			throw new IllegalArgumentException("Payment can be verified only for a pending booking");
		}

		if (booking.razorpayOrderId() == null || !booking.razorpayOrderId().equals(request.getRazorpayOrderId())) {
			throw new PaymentException("Razorpay order does not match this booking");
		}

		try {

			String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();

			boolean signatureValid = Utils.verifySignature(payload, request.getRazorpaySignature(), razorpayKeySecret);

			if (!signatureValid) {
				throw new PaymentException("Invalid Razorpay payment signature");
			}

		} catch (PaymentException e) {
			throw e;
		} catch (Exception e) {
			throw new PaymentException("Razorpay payment verification failed: " + e.getMessage());
		}

		Payment payment = Payment.builder().bookingId(booking.bookingId()).userId(userId).amount(booking.totalAmount())
				.paymentMethod(null).transactionId(request.getRazorpayPaymentId()).status(PaymentStatus.SUCCESS)
				.paymentDate(LocalDateTime.now()).build();

		Payment savedPayment = paymentRepository.save(payment);

		// booking-service confirms the booking and asks ticket-service to issue the
		// tickets.
		// If that fails, this transaction rolls back too and the customer can simply
		// call verify again.
		bookingClient.confirmBooking(booking.bookingId());

		return mapToResponse(savedPayment);
	}

	// ==================================================
	// READ
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public PaymentResponse getPaymentById(Long id, Long userId) {

		Payment payment = paymentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

		assertOwner(payment, userId);

		return mapToResponse(payment);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PaymentResponse> getPaymentsByBooking(Long bookingId, Long userId) {

		List<Payment> payments = paymentRepository.findByBookingId(bookingId);

		for (Payment payment : payments) {
			assertOwner(payment, userId);
		}

		return payments.stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentResponse getPaymentByTransaction(String transactionId, Long userId) {

		Payment payment = paymentRepository.findByTransactionId(transactionId).orElseThrow(
				() -> new ResourceNotFoundException("Payment not found with transaction id: " + transactionId));

		assertOwner(payment, userId);

		return mapToResponse(payment);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PaymentResponse> getMyRecentPayments(Long userId, int limit) {
		return paymentRepository.findByUserIdOrderByPaymentDateDesc(userId).stream().limit(limit)
				.map(this::mapToResponse).toList();
	}

	private void assertOwner(Payment payment, Long userId) {
		if (!payment.getUserId().equals(userId)) {
			throw new AccessDeniedException("You cannot view another user's payment");
		}
	}

	private PaymentResponse mapToResponse(Payment payment) {
		return new PaymentResponse(payment.getId(), payment.getBookingId(), payment.getAmount(),
				payment.getPaymentMethod(), payment.getTransactionId(), payment.getStatus(), payment.getPaymentDate());
	}
}