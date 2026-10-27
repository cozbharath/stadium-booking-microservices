package com.stadiumbooking.auth.otp;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

	/**
	 * Locks the row while it is checked, so many parallel guesses cannot all read
	 * "0 wrong attempts" and slip past the attempt limit.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select o from OtpVerification o where o.mobile = :mobile and o.purpose = :purpose")
	Optional<OtpVerification> findForUpdate(@Param("mobile") String mobile, @Param("purpose") OtpPurpose purpose);

	@Modifying
	@Query("delete from OtpVerification o where o.expiresAt < :cutoff")
	void deleteExpiredBefore(@Param("cutoff") LocalDateTime cutoff);
}
