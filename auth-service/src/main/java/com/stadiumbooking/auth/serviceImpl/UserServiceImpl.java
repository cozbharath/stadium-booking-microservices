package com.stadiumbooking.auth.serviceImpl;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stadiumbooking.auth.dto.user.request.ChangePasswordRequest;
import com.stadiumbooking.auth.dto.user.request.UserRequest;
import com.stadiumbooking.auth.dto.user.response.UserResponse;
import com.stadiumbooking.auth.entity.User;
import com.stadiumbooking.auth.repository.UserRepository;
import com.stadiumbooking.auth.util.MobileNumber;
import com.stadiumbooking.auth.service.UserService;
import com.stadiumbooking.common.enums.Role;
import com.stadiumbooking.common.exception.BookingException;
import com.stadiumbooking.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserResponse createUser(UserRequest request) {

		if (request == null || request.getName() == null || request.getName().isBlank()) {
			throw new IllegalArgumentException("Name is required");
		}

		if (request.getEmail() == null || request.getEmail().isBlank()) {
			throw new IllegalArgumentException("Email is required");
		}

		if (request.getRole() == null) {
			throw new IllegalArgumentException("Role is required");
		}

		String mobile = MobileNumber.normalize(request.getMobile());

		boolean needsPassword = request.getRole() != Role.CUSTOMER;

		if (needsPassword && (request.getPassword() == null || request.getPassword().isBlank())) {
			throw new IllegalArgumentException("Password is required for admin and staff accounts");
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("A user with this email already exists");
		}

		if (userRepository.existsByMobile(mobile)) {
			throw new IllegalArgumentException("A user with this mobile number already exists");
		}

		User user = new User();

		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setMobile(mobile);
		user.setPassword(needsPassword ? passwordEncoder.encode(request.getPassword()) : null);
		user.setRole(request.getRole());

		return mapToResponse(userRepository.save(user));
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUserById(Long id) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

		return mapToResponse(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> getAllUsers() {

		return userRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional
	public UserResponse updateUser(Long id, UserRequest request, Long currentUserId) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

		if (request.getName() == null || request.getName().isBlank()) {
			throw new IllegalArgumentException("Name is required");
		}

		if (request.getEmail() == null || request.getEmail().isBlank()) {
			throw new IllegalArgumentException("Email is required");
		}

		if (request.getRole() == null) {
			throw new IllegalArgumentException("Role is required");
		}

		String mobile = MobileNumber.normalize(request.getMobile());

		userRepository.findByMobile(mobile).filter(other -> !other.getId().equals(id)).ifPresent(other -> {
			throw new IllegalArgumentException("A user with this mobile number already exists");
		});

		// don't let the last admin be demoted, or nobody can manage users any more
		boolean losingAdminRole = user.getRole() == Role.ADMIN && request.getRole() != Role.ADMIN;

		if (losingAdminRole && userRepository.countByRole(Role.ADMIN) <= 1) {
			throw new BookingException("Cannot remove admin rights from the last remaining admin");
		}

		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setMobile(mobile);
		user.setRole(request.getRole());

		if (request.getRole() == Role.CUSTOMER) {

			// customers sign in with an OTP and have no password
			user.setPassword(null);

		} else if (request.getPassword() != null && !request.getPassword().isBlank()) {

			// admin password reset: only changes the password if one is provided
			user.setPassword(passwordEncoder.encode(request.getPassword()));

		} else if (user.getPassword() == null) {

			throw new IllegalArgumentException("Password is required for admin and staff accounts");
		}

		return mapToResponse(userRepository.save(user));
	}

	@Override
	@Transactional
	public void deleteUser(Long id, Long currentUserId) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

		if (currentUserId != null && currentUserId.equals(id)) {
			throw new AccessDeniedException("You cannot delete your own account");
		}

		if (user.getRole() == Role.ADMIN && userRepository.countByRole(Role.ADMIN) <= 1) {
			throw new BookingException("Cannot delete the last remaining admin");
		}

		userRepository.delete(user);
	}

	@Override
	@Transactional
	public void changePassword(Long userId, ChangePasswordRequest request) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		if (user.getPassword() == null) {
			throw new AccessDeniedException("This account has no password. It signs in with an OTP.");
		}

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			throw new AccessDeniedException("Current password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));

		userRepository.save(user);
	}

	private UserResponse mapToResponse(User user) {

		return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getMobile(), user.getRole());
	}
}