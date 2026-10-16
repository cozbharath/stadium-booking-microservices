package com.stadiumbooking.venue.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stadiumbooking.common.exception.ResourceNotFoundException;
import com.stadiumbooking.venue.dto.standtype.request.StandTypeRequest;
import com.stadiumbooking.venue.dto.standtype.response.StandTypeResponse;
import com.stadiumbooking.venue.entity.StandType;
import com.stadiumbooking.venue.repository.StandTypeRepository;
import com.stadiumbooking.venue.service.StandTypeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StandTypeServiceImpl implements StandTypeService {

	private final StandTypeRepository standTypeRepository;

	@Override
	@Transactional
	public StandTypeResponse createStandType(StandTypeRequest request) {

		StandType standType = new StandType();

		standType.setName(request.getName());
		standType.setDescription(request.getDescription());

		return mapToResponse(standTypeRepository.save(standType));
	}

	@Override
	@Transactional(readOnly = true)
	public StandTypeResponse getStandTypeById(Long id) {

		StandType standType = standTypeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stand type not found with id: " + id));

		return mapToResponse(standType);
	}

	@Override
	@Transactional(readOnly = true)
	public List<StandTypeResponse> getAllStandTypes() {

		return standTypeRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional
	public StandTypeResponse updateStandType(Long id, StandTypeRequest request) {

		StandType standType = standTypeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stand type not found with id: " + id));

		standType.setName(request.getName());
		standType.setDescription(request.getDescription());

		return mapToResponse(standTypeRepository.save(standType));
	}

	@Override
	@Transactional
	public void deleteStandType(Long id) {

		StandType standType = standTypeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stand type not found with id: " + id));

		standTypeRepository.delete(standType);
	}

	private StandTypeResponse mapToResponse(StandType standType) {

		return new StandTypeResponse(standType.getId(), standType.getName(), standType.getDescription());
	}
}