package com.stadiumbooking.venue.service;

import java.util.List;

import com.stadiumbooking.venue.dto.standtype.request.StandTypeRequest;
import com.stadiumbooking.venue.dto.standtype.response.StandTypeResponse;

public interface StandTypeService {

	StandTypeResponse createStandType(StandTypeRequest request);

	StandTypeResponse getStandTypeById(Long id);

	List<StandTypeResponse> getAllStandTypes();

	StandTypeResponse updateStandType(Long id, StandTypeRequest request);

	void deleteStandType(Long id);
}