package com.stadiumbooking.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stadiumbooking.payment.entity.Payment;
import com.stadiumbooking.payment.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByBookingId(Long bookingId);

	Optional<Payment> findByTransactionId(String transactionId);

	List<Payment> findByStatus(PaymentStatus status);

	List<Payment> findByUserIdOrderByPaymentDateDesc(Long userId);
}