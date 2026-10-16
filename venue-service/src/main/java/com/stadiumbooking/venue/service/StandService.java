package com.stadiumbooking.venue.service;

import java.util.List;

import com.stadiumbooking.venue.dto.stand.request.StandRequest;
import com.stadiumbooking.venue.dto.stand.response.StandResponse;

public interface StandService {

	StandResponse createStand(StandRequest request);

	StandResponse getStandById(Long id);

	List<StandResponse> getAllStands();

	List<StandResponse> getStandsByStadium(Long stadiumId);

	StandResponse updateStand(Long id, StandRequest request);

	void deleteStand(Long id);
}