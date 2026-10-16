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

import com.stadiumbooking.venue.dto.stadium.request.StadiumRequest;
import com.stadiumbooking.venue.dto.stadium.response.StadiumResponse;
import com.stadiumbooking.venue.service.StadiumService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stadiums")
@RequiredArgsConstructor
public class StadiumController {

	private final StadiumService stadiumService;

	@PostMapping
	public ResponseEntity<StadiumResponse> createStadium(@RequestBody StadiumRequest request) {

		return new ResponseEntity<>(stadiumService.createStadium(request), HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<StadiumResponse> getStadiumById(@PathVariable Long id) {

		return ResponseEntity.ok(stadiumService.getStadiumById(id));
	}

	@GetMapping
	public ResponseEntity<List<StadiumResponse>> getAllStadiums() {

		return ResponseEntity.ok(stadiumService.getAllStadiums());
	}

	@PutMapping("/{id}")
	public ResponseEntity<StadiumResponse> updateStadium(@PathVariable Long id, @RequestBody StadiumRequest request) {

		return ResponseEntity.ok(stadiumService.updateStadium(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStadium(@PathVariable Long id) {

		stadiumService.deleteStadium(id);

		return ResponseEntity.noContent().build();
	}
}