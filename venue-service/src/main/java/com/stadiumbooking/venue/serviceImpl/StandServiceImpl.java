package com.stadiumbooking.venue.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stadiumbooking.common.exception.ResourceNotFoundException;
import com.stadiumbooking.venue.dto.stand.request.StandRequest;
import com.stadiumbooking.venue.dto.stand.response.StandResponse;
import com.stadiumbooking.venue.entity.Stadium;
import com.stadiumbooking.venue.entity.Stand;
import com.stadiumbooking.venue.entity.StandType;
import com.stadiumbooking.venue.repository.StadiumRepository;
import com.stadiumbooking.venue.repository.StandRepository;
import com.stadiumbooking.venue.repository.StandTypeRepository;
import com.stadiumbooking.venue.service.StandService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StandServiceImpl implements StandService {

	private final StandRepository standRepository;
	private final StadiumRepository stadiumRepository;
	private final StandTypeRepository standTypeRepository;

	@Override
	@Transactional
	public StandResponse createStand(StandRequest request) {

		Stadium stadium = stadiumRepository.findById(request.getStadiumId()).orElseThrow(
				() -> new ResourceNotFoundException("Stadium not found with id: " + request.getStadiumId()));

		StandType standType = standTypeRepository.findById(request.getStandTypeId()).orElseThrow(
				() -> new ResourceNotFoundException("Stand type not found with id: " + request.getStandTypeId()));

		Stand stand = new Stand();

		stand.setName(request.getName());
		stand.setStadium(stadium);
		stand.setStandType(standType);

		return mapToResponse(standRepository.save(stand));
	}

	@Override
	@Transactional(readOnly = true)
	public StandResponse getStandById(Long id) {

		Stand stand = standRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stand not found with id: " + id));

		return mapToResponse(stand);
	}

	@Override
	@Transactional(readOnly = true)
	public List<StandResponse> getAllStands() {

		return standRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<StandResponse> getStandsByStadium(Long stadiumId) {

		return standRepository.findByStadiumId(stadiumId).stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional
	public StandResponse updateStand(Long id, StandRequest request) {

		Stand stand = standRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stand not found with id: " + id));

		Stadium stadium = stadiumRepository.findById(request.getStadiumId()).orElseThrow(
				() -> new ResourceNotFoundException("Stadium not found with id: " + request.getStadiumId()));

		StandType standType = standTypeRepository.findById(request.getStandTypeId()).orElseThrow(
				() -> new ResourceNotFoundException("Stand type not found with id: " + request.getStandTypeId()));

		stand.setName(request.getName());
		stand.setStadium(stadium);
		stand.setStandType(standType);

		return mapToResponse(standRepository.save(stand));
	}

	@Override
	@Transactional
	public void deleteStand(Long id) {

		Stand stand = standRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stand not found with id: " + id));

		standRepository.delete(stand);
	}

	private StandResponse mapToResponse(Stand stand) {

		return new StandResponse(stand.getId(), stand.getName(), stand.getStadium().getId(),
				stand.getStadium().getName(), stand.getStandType().getId(), stand.getStandType().getName());
	}
}