package com.stadiumbooking.auth.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Used by both register (step 2) and customer sign in (step 2). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {

	@NotBlank(message = "Mobile number is required")
	private String mobile;

	@NotBlank(message = "OTP is required")
	private String otp;
}
