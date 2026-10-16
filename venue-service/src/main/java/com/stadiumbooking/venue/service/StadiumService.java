package com.stadiumbooking.venue.service;

import java.util.List;

import com.stadiumbooking.venue.dto.stadium.request.StadiumRequest;
import com.stadiumbooking.venue.dto.stadium.response.StadiumResponse;

public interface StadiumService {

	StadiumResponse createStadium(StadiumRequest request);

	StadiumResponse getStadiumById(Long id);

	List<StadiumResponse> getAllStadiums();

	StadiumResponse updateStadium(Long id, StadiumRequest request);

	void deleteStadium(Long id);
}