package com.stadiumbooking.venue.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.venue.dto.stand.request.StandRequest;
import com.stadiumbooking.venue.dto.stand.response.StandResponse;
import com.stadiumbooking.venue.service.StandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stands")
@RequiredArgsConstructor
public class StandController {

	private final StandService standService;

	@PostMapping
	public ResponseEntity<StandResponse> createStand(@RequestBody StandRequest request) {

		return new ResponseEntity<>(standService.createStand(request), HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<StandResponse> getStandById(@PathVariable Long id) {

		return ResponseEntity.ok(standService.getStandById(id));
	}

	@GetMapping
	public ResponseEntity<List<StandResponse>> getAllStands() {

		return ResponseEntity.ok(standService.getAllStands());
	}

	@GetMapping("/stadium/{stadiumId}")
	public ResponseEntity<List<StandResponse>> getStandsByStadium(@PathVariable Long stadiumId) {

		return ResponseEntity.ok(standService.getStandsByStadium(stadiumId));
	}

	@PutMapping("/{id}")
	public ResponseEntity<StandResponse> updateStand(@PathVariable Long id, @RequestBody StandRequest request) {

		return ResponseEntity.ok(standService.updateStand(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStand(@PathVariable Long id) {

		standService.deleteStand(id);

		return ResponseEntity.noContent().build();
	}
}