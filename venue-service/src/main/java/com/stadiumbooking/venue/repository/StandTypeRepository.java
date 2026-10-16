package com.stadiumbooking.venue.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stadiumbooking.venue.entity.StandType;

public interface StandTypeRepository extends JpaRepository<StandType, Long> {
}