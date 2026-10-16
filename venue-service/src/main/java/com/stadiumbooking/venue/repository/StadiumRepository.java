package com.stadiumbooking.venue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stadiumbooking.venue.entity.Stadium;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, Long> {
}