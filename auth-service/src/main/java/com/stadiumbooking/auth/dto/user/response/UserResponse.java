package com.stadiumbooking.auth.dto.user.response;

import com.stadiumbooking.common.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

	private Long id;

	private String name;

	private String email;

	private Role role;
}
