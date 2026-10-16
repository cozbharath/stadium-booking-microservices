package com.stadiumbooking.venue.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stadiumbooking.common.exception.ResourceNotFoundException;
import com.stadiumbooking.venue.dto.stadium.request.StadiumRequest;
import com.stadiumbooking.venue.dto.stadium.response.StadiumResponse;
import com.stadiumbooking.venue.entity.Stadium;
import com.stadiumbooking.venue.repository.StadiumRepository;
import com.stadiumbooking.venue.service.StadiumService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StadiumServiceImpl implements StadiumService {

	private final StadiumRepository stadiumRepository;

	@Override
	@Transactional
	public StadiumResponse createStadium(StadiumRequest request) {

		Stadium stadium = new Stadium();

		stadium.setName(request.getName());
		stadium.setLocation(request.getLocation());
		stadium.setCapacity(request.getCapacity());

		return mapToResponse(stadiumRepository.save(stadium));
	}

	@Override
	@Transactional(readOnly = true)
	public StadiumResponse getStadiumById(Long id) {

		Stadium stadium = stadiumRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stadium not found with id: " + id));

		return mapToResponse(stadium);
	}

	@Override
	@Transactional(readOnly = true)
	public List<StadiumResponse> getAllStadiums() {

		return stadiumRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional
	public StadiumResponse updateStadium(Long id, StadiumRequest request) {

		Stadium stadium = stadiumRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stadium not found with id: " + id));

		stadium.setName(request.getName());
		stadium.setLocation(request.getLocation());
		stadium.setCapacity(request.getCapacity());

		return mapToResponse(stadiumRepository.save(stadium));
	}

	@Override
	@Transactional
	public void deleteStadium(Long id) {

		Stadium stadium = stadiumRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Stadium not found with id: " + id));

		stadiumRepository.delete(stadium);
	}

	private StadiumResponse mapToResponse(Stadium stadium) {

		return new StadiumResponse(stadium.getId(), stadium.getName(), stadium.getLocation(), stadium.getCapacity());
	}
}