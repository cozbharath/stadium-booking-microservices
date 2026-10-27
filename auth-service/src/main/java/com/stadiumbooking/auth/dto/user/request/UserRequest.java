package com.stadiumbooking.auth.dto.user.request;

import com.stadiumbooking.common.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

	private String name;

	private String email;

	private String mobile;

	private Role role;

	private String password;
}
