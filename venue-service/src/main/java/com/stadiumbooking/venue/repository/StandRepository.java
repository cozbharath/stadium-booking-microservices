package com.stadiumbooking.venue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stadiumbooking.venue.entity.Stand;

@Repository
public interface StandRepository extends JpaRepository<Stand, Long> {

	List<Stand> findByStadiumId(Long stadiumId);
}