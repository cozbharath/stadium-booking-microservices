package com.stadiumbooking.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.common.security.CustomUserDetails;
import com.stadiumbooking.payment.dto.payment.request.CreateOrderRequest;
import com.stadiumbooking.payment.dto.payment.request.VerifyPaymentRequest;
import com.stadiumbooking.payment.dto.payment.response.PaymentResponse;
import com.stadiumbooking.payment.dto.payment.response.RazorpayOrderResponse;
import com.stadiumbooking.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/create-order")
	public ResponseEntity<RazorpayOrderResponse> createRazorpayOrder(@RequestBody CreateOrderRequest request,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		RazorpayOrderResponse response = paymentService.createRazorpayOrder(request, userDetails.getId());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/verify")
	public ResponseEntity<PaymentResponse> verifyPayment(@RequestBody VerifyPaymentRequest request,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		PaymentResponse response = paymentService.verifyRazorpayPayment(request, userDetails.getId());

		return ResponseEntity.ok(response);
	}

	// Recent orders (profile modal). Kept above "/{id}" to avoid any ambiguity.
	@GetMapping("/my/recent")
	public ResponseEntity<List<PaymentResponse>> getMyRecentPayments(
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		return ResponseEntity.ok(paymentService.getMyRecentPayments(userDetails.getId(), 10));
	}

	@GetMapping("/{id}")
	public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		return ResponseEntity.ok(paymentService.getPaymentById(id, userDetails.getId()));
	}

	@GetMapping("/booking/{bookingId}")
	public ResponseEntity<List<PaymentResponse>> getPaymentsByBooking(@PathVariable Long bookingId,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		return ResponseEntity.ok(paymentService.getPaymentsByBooking(bookingId, userDetails.getId()));
	}

	@GetMapping("/transaction/{transactionId}")
	public ResponseEntity<PaymentResponse> getPaymentByTransaction(@PathVariable String transactionId,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		return ResponseEntity.ok(paymentService.getPaymentByTransaction(transactionId, userDetails.getId()));
	}
}