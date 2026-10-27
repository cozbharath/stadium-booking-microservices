package com.stadiumbooking.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stadiumbooking.auth.entity.User;
import com.stadiumbooking.common.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<User> findByMobile(String mobile);

	boolean existsByMobile(String mobile);

	long countByRole(Role role);
}
