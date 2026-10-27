package com.stadiumbooking.auth.entity;

import com.stadiumbooking.common.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	/**
	 * 10 digit Indian mobile number. Nullable only so that accounts created
	 * before this field existed keep working; new accounts always have one.
	 * MySQL allows many NULL values in a unique column.
	 */
	@Column(unique = true, length = 15)
	private String mobile;

	/** Only ADMIN and STAFF have a password. Customers sign in with an OTP. */
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;
}
