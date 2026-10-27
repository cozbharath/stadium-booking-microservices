package com.stadiumbooking.auth.dto.auth.response;

import com.stadiumbooking.common.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

	private String token;

	private Long userId;

	private String name;

	private String email;

	private Role role;

	private String mobile;
}