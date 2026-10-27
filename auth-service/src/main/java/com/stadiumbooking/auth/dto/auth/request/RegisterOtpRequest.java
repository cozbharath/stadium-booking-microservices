package com.stadiumbooking.auth.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Step 1 of customer registration: who is registering. An OTP is texted to the mobile number. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterOtpRequest {

	@NotBlank(message = "Name is required")
	@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Enter a valid email address")
	private String email;

	@NotBlank(message = "Mobile number is required")
	private String mobile;
}
