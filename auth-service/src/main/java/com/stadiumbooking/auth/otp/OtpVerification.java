package com.stadiumbooking.auth.otp;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The OTP that is currently waiting for one mobile number and purpose.
 * There is at most one row per (mobile, purpose); a new request reuses it.
 * The OTP itself is never stored, only a keyed hash of it.
 */
@Entity
@Table(name = "otp_verification", uniqueConstraints = @UniqueConstraint(columnNames = { "mobile", "purpose" }))
@Getter
@Setter
@NoArgsConstructor
public class OtpVerification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 15)
	private String mobile;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OtpPurpose purpose;

	/** HMAC-SHA256 of the OTP as lowercase hex (64 characters). */
	@Column(nullable = false, length = 64)
	private String otpHash;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	/** Wrong guesses for the current OTP. */
	private int attempts;

	/** How many OTPs were sent inside the current rate-limit window. */
	private int sendCount;

	private LocalDateTime windowStart;

	private LocalDateTime lastSentAt;

	/** Registration details waiting for the OTP to be confirmed (REGISTER only). */
	private String pendingName;

	private String pendingEmail;
}
