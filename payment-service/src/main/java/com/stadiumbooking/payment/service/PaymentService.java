package com.stadiumbooking.payment.service;

import java.util.List;

import com.stadiumbooking.payment.dto.payment.request.CreateOrderRequest;
import com.stadiumbooking.payment.dto.payment.request.VerifyPaymentRequest;
import com.stadiumbooking.payment.dto.payment.response.PaymentResponse;
import com.stadiumbooking.payment.dto.payment.response.RazorpayOrderResponse;

public interface PaymentService {

	RazorpayOrderResponse createRazorpayOrder(CreateOrderRequest request, Long userId);

	PaymentResponse verifyRazorpayPayment(VerifyPaymentRequest request, Long userId);

	PaymentResponse getPaymentById(Long id, Long userId);

	List<PaymentResponse> getPaymentsByBooking(Long bookingId, Long userId);

	PaymentResponse getPaymentByTransaction(String transactionId, Long userId);

	List<PaymentResponse> getMyRecentPayments(Long userId, int limit);
}