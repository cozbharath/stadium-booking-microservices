package com.stadiumbooking.auth.service;

import java.util.List;

import com.stadiumbooking.auth.dto.user.request.ChangePasswordRequest;
import com.stadiumbooking.auth.dto.user.request.UserRequest;
import com.stadiumbooking.auth.dto.user.response.UserResponse;

public interface UserService {

	UserResponse createUser(UserRequest request);

	UserResponse getUserById(Long id);

	List<UserResponse> getAllUsers();

	UserResponse updateUser(Long id, UserRequest request, Long currentUserId);

	void deleteUser(Long id, Long currentUserId);

	void changePassword(Long userId, ChangePasswordRequest request);
}
