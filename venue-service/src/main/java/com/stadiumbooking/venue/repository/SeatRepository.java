package com.stadiumbooking.venue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stadiumbooking.venue.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {

	List<Seat> findByStandId(Long standId);

}