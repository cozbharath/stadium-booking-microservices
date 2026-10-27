package com.stadiumbooking.auth.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Customer sign in, step 1: ask for an OTP. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginOtpRequest {

	@NotBlank(message = "Mobile number is required")
	private String mobile;
}
