package com.stadiumbooking.venue.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stadiumbooking.common.exception.ResourceNotFoundException;
import com.stadiumbooking.venue.dto.seat.request.BulkSeatRequest;
import com.stadiumbooking.venue.dto.seat.request.SeatRequest;
import com.stadiumbooking.venue.dto.seat.response.SeatResponse;
import com.stadiumbooking.venue.entity.Seat;
import com.stadiumbooking.venue.entity.Stand;
import com.stadiumbooking.venue.repository.SeatRepository;
import com.stadiumbooking.venue.repository.StandRepository;
import com.stadiumbooking.venue.service.SeatService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

	private final SeatRepository seatRepository;
	private final StandRepository standRepository;

	@Override
	@Transactional
	public SeatResponse createSeat(SeatRequest request) {

		Stand stand = standRepository.findById(request.getStandId())
				.orElseThrow(() -> new ResourceNotFoundException("Stand not found with id: " + request.getStandId()));

		Seat seat = new Seat();

		seat.setStand(stand);
		seat.setRowNumber(request.getRowNumber());
		seat.setSeatNumber(request.getSeatNumber());

		return mapToResponse(seatRepository.save(seat));
	}

	@Override
	@Transactional(readOnly = true)
	public SeatResponse getSeatById(Long id) {

		Seat seat = seatRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + id));

		return mapToResponse(seat);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SeatResponse> getAllSeats() {

		return seatRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SeatResponse> getSeatsByStand(Long standId) {

		return seatRepository.findByStandId(standId).stream().map(this::mapToResponse).toList();
	}

	@Override
	@Transactional
	public SeatResponse updateSeat(Long id, SeatRequest request) {

		Seat seat = seatRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + id));

		Stand stand = standRepository.findById(request.getStandId())
				.orElseThrow(() -> new ResourceNotFoundException("Stand not found with id: " + request.getStandId()));

		seat.setStand(stand);
		seat.setRowNumber(request.getRowNumber());
		seat.setSeatNumber(request.getSeatNumber());

		return mapToResponse(seatRepository.save(seat));
	}

	@Override
	@Transactional
	public void deleteSeat(Long id) {

		Seat seat = seatRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + id));

		seatRepository.delete(seat);
	}

	private SeatResponse mapToResponse(Seat seat) {

		return new SeatResponse(seat.getId(), seat.getStand().getId(), seat.getStand().getName(), seat.getRowNumber(),
				seat.getSeatNumber());
	}

	@Override
	@Transactional
	public List<SeatResponse> createSeatsBulk(BulkSeatRequest request) {

		Stand stand = standRepository.findById(request.getStandId())
				.orElseThrow(() -> new ResourceNotFoundException("Stand not found with id: " + request.getStandId()));

		if (request.getRows() == null || request.getRows().isEmpty()) {
			throw new IllegalArgumentException("At least one row is required");
		}
		if (request.getSeatStart() == null || request.getSeatEnd() == null
				|| request.getSeatStart() > request.getSeatEnd()) {
			throw new IllegalArgumentException("Invalid seat number range");
		}

		java.util.Set<String> existingKeys = seatRepository.findByStandId(request.getStandId()).stream()
				.map(s -> s.getRowNumber() + "-" + s.getSeatNumber()).collect(java.util.stream.Collectors.toSet());

		java.util.List<Seat> toCreate = new java.util.ArrayList<>();

		for (String row : request.getRows()) {
			for (int num = request.getSeatStart(); num <= request.getSeatEnd(); num++) {
				String key = row + "-" + num;
				if (!existingKeys.contains(key)) {
					Seat seat = new Seat();
					seat.setStand(stand);
					seat.setRowNumber(row);
					seat.setSeatNumber(num);
					toCreate.add(seat);
					existingKeys.add(key);
				}
			}
		}

		return seatRepository.saveAll(toCreate).stream().map(this::mapToResponse).toList();
	}
}