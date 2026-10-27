package com.stadiumbooking.auth.otp;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stadiumbooking.auth.dto.auth.response.OtpSentResponse;
import com.stadiumbooking.auth.entity.User;
import com.stadiumbooking.auth.exception.AuthFlowException;
import com.stadiumbooking.auth.repository.UserRepository;
import com.stadiumbooking.auth.sms.SmsSender;
import com.stadiumbooking.auth.util.MobileNumber;
import com.stadiumbooking.common.enums.Role;

import lombok.RequiredArgsConstructor;

/**
 * One time passwords for customers: registration and sign in.
 *
 * Wrong OTPs and rate limit hits throw AuthFlowException. Those are listed in
 * noRollbackFor so the attempt counters are still saved. If the SMS provider
 * fails, SmsDeliveryException is thrown and the whole change is rolled back, so
 * a failed text does not use up the customer's send allowance.
 */
@Service
@RequiredArgsConstructor
@Transactional(noRollbackFor = AuthFlowException.class)
public class OtpService {

	private static final String GENERIC_LOGIN_MESSAGE = "If this number is registered, an OTP has been sent.";

	private final OtpVerificationRepository otpRepository;

	private final UserRepository userRepository;

	private final SmsSender smsSender;

	private final OtpProperties props;

	private final Clock clock;

	// the daily cap is kept in memory, it restarts from zero when the service restarts
	private LocalDate dailyDate;

	private int dailyCount;

	// ==================================================
	// REGISTER
	// ==================================================

	public OtpSentResponse sendRegisterOtp(String name, String email, String rawMobile) {

		String mobile = MobileNumber.normalize(rawMobile);

		if (userRepository.existsByEmail(email)) {
			throw new AuthFlowException(HttpStatus.CONFLICT, "Email already registered");
		}

		if (userRepository.existsByMobile(mobile)) {
			throw new AuthFlowException(HttpStatus.CONFLICT,
					"This mobile number is already registered. Please sign in instead.");
		}

		return issue(mobile, OtpPurpose.REGISTER, name.trim(), email.trim(), "OTP sent to your mobile number.");
	}

	/** Checks the OTP and creates the customer account. */
	public User verifyRegisterOtp(String rawMobile, String otp) {

		String mobile = MobileNumber.normalize(rawMobile);

		OtpVerification row = checkOtp(mobile, OtpPurpose.REGISTER, otp);

		// somebody may have taken the email or number since the OTP was sent
		if (userRepository.existsByEmail(row.getPendingEmail())) {
			throw new AuthFlowException(HttpStatus.CONFLICT, "Email already registered");
		}

		if (userRepository.existsByMobile(mobile)) {
			throw new AuthFlowException(HttpStatus.CONFLICT,
					"This mobile number is already registered. Please sign in instead.");
		}

		User user = new User();

		user.setName(row.getPendingName());
		user.setEmail(row.getPendingEmail());
		user.setMobile(mobile);
		user.setRole(Role.CUSTOMER);
		user.setPassword(null);

		User saved = userRepository.save(user);

		otpRepository.delete(row);

		return saved;
	}

	// ==================================================
	// LOGIN (customers only)
	// ==================================================

	/** Always answers the same, so the response does not reveal which numbers are registered. */
	public OtpSentResponse sendLoginOtp(String rawMobile) {

		String mobile = MobileNumber.normalize(rawMobile);

		User user = userRepository.findByMobile(mobile).orElse(null);

		if (user == null || user.getRole() != Role.CUSTOMER) {

			return new OtpSentResponse(GENERIC_LOGIN_MESSAGE, props.getTtlSeconds(), props.getResendCooldownSeconds(),
					MobileNumber.mask(mobile));
		}

		issue(mobile, OtpPurpose.LOGIN, null, null, GENERIC_LOGIN_MESSAGE);

		return new OtpSentResponse(GENERIC_LOGIN_MESSAGE, props.getTtlSeconds(), props.getResendCooldownSeconds(),
				MobileNumber.mask(mobile));
	}

	public User verifyLoginOtp(String rawMobile, String otp) {

		String mobile = MobileNumber.normalize(rawMobile);

		OtpVerification row = checkOtp(mobile, OtpPurpose.LOGIN, otp);

		User user = userRepository.findByMobile(mobile).filter(u -> u.getRole() == Role.CUSTOMER)
				.orElseThrow(() -> new AuthFlowException(HttpStatus.UNAUTHORIZED, "Incorrect OTP."));

		otpRepository.delete(row);

		return user;
	}

	// ==================================================
	// SHARED
	// ==================================================

	private OtpSentResponse issue(String mobile, OtpPurpose purpose, String pendingName, String pendingEmail,
			String message) {

		LocalDateTime now = LocalDateTime.now(clock);

		// tidy up old rows now and then, so the table does not grow forever
		otpRepository.deleteExpiredBefore(now.minusDays(1));

		OtpVerification row = otpRepository.findForUpdate(mobile, purpose).orElse(null);

		if (row == null) {

			row = new OtpVerification();
			row.setMobile(mobile);
			row.setPurpose(purpose);
			row.setWindowStart(now);
			row.setSendCount(0);

		} else {

			if (row.getLastSentAt() != null) {

				long waited = Duration.between(row.getLastSentAt(), now).getSeconds();
				long remaining = props.getResendCooldownSeconds() - waited;

				if (remaining > 0) {
					throw new AuthFlowException(HttpStatus.TOO_MANY_REQUESTS,
							"Please wait " + remaining + " seconds before asking for another OTP.");
				}
			}

			LocalDateTime windowEnd = row.getWindowStart().plusSeconds(props.getWindowSeconds());

			if (now.isBefore(windowEnd)) {

				if (row.getSendCount() >= props.getMaxSendsPerWindow()) {

					long minutes = Math.max(1, Duration.between(now, windowEnd).toMinutes() + 1);

					throw new AuthFlowException(HttpStatus.TOO_MANY_REQUESTS,
							"Too many OTP requests. Please try again in " + minutes + " minute(s).");
				}

			} else {

				row.setWindowStart(now);
				row.setSendCount(0);
			}
		}

		checkDailyLimit(now.toLocalDate());

		String otp = OtpCodec.generate(props.getLength());

		row.setOtpHash(OtpCodec.hash(props.getSecret(), mobile, purpose.name(), otp));
		row.setExpiresAt(now.plusSeconds(props.getTtlSeconds()));
		row.setAttempts(0);
		row.setSendCount(row.getSendCount() + 1);
		row.setLastSentAt(now);
		row.setPendingName(pendingName);
		row.setPendingEmail(pendingEmail);

		otpRepository.saveAndFlush(row);

		// if this throws SmsDeliveryException the saved row is rolled back
		smsSender.sendOtp(mobile, otp, Math.max(1, props.getTtlSeconds() / 60));

		recordDailySend();

		return new OtpSentResponse(message, props.getTtlSeconds(), props.getResendCooldownSeconds(),
				MobileNumber.mask(mobile));
	}

	/** Validates the OTP and returns the row. Throws AuthFlowException when it is not valid. */
	private OtpVerification checkOtp(String mobile, OtpPurpose purpose, String otp) {

		if (otp == null || !otp.trim().matches("\\d{" + props.getLength() + "}")) {
			throw new AuthFlowException(HttpStatus.BAD_REQUEST,
					"Enter the " + props.getLength() + " digit OTP sent to your mobile.");
		}

		OtpVerification row = otpRepository.findForUpdate(mobile, purpose).orElseThrow(
				() -> new AuthFlowException(HttpStatus.BAD_REQUEST, "No OTP found. Please request a new one."));

		if (row.getAttempts() >= props.getMaxAttempts()) {
			throw new AuthFlowException(HttpStatus.TOO_MANY_REQUESTS,
					"Too many wrong attempts. Please request a new OTP.");
		}

		LocalDateTime now = LocalDateTime.now(clock);

		if (!now.isBefore(row.getExpiresAt())) {
			throw new AuthFlowException(HttpStatus.BAD_REQUEST, "OTP has expired. Please request a new one.");
		}

		String actual = OtpCodec.hash(props.getSecret(), mobile, purpose.name(), otp.trim());

		if (!OtpCodec.matches(row.getOtpHash(), actual)) {

			row.setAttempts(row.getAttempts() + 1);

			otpRepository.save(row);

			int left = props.getMaxAttempts() - row.getAttempts();

			if (left <= 0) {
				throw new AuthFlowException(HttpStatus.TOO_MANY_REQUESTS,
						"Too many wrong attempts. Please request a new OTP.");
			}

			throw new AuthFlowException(HttpStatus.UNAUTHORIZED, "Incorrect OTP. " + left + " attempt(s) left.");
		}

		return row;
	}

	private synchronized void checkDailyLimit(LocalDate today) {

		if (!today.equals(dailyDate)) {
			dailyDate = today;
			dailyCount = 0;
		}

		if (dailyCount >= props.getDailySendLimit()) {
			throw new AuthFlowException(HttpStatus.TOO_MANY_REQUESTS,
					"We are getting too many requests right now. Please try again later.");
		}
	}

	private synchronized void recordDailySend() {

		dailyCount++;
	}
}
