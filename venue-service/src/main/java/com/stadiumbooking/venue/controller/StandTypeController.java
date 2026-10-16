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

import com.stadiumbooking.venue.dto.standtype.request.StandTypeRequest;
import com.stadiumbooking.venue.dto.standtype.response.StandTypeResponse;
import com.stadiumbooking.venue.service.StandTypeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stand-types")
@RequiredArgsConstructor
public class StandTypeController {

	private final StandTypeService standTypeService;

	@PostMapping
	public ResponseEntity<StandTypeResponse> createStandType(@RequestBody StandTypeRequest request) {

		return new ResponseEntity<>(standTypeService.createStandType(request), HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<StandTypeResponse> getStandTypeById(@PathVariable Long id) {

		return ResponseEntity.ok(standTypeService.getStandTypeById(id));
	}

	@GetMapping
	public ResponseEntity<List<StandTypeResponse>> getAllStandTypes() {

		return ResponseEntity.ok(standTypeService.getAllStandTypes());
	}

	@PutMapping("/{id}")
	public ResponseEntity<StandTypeResponse> updateStandType(@PathVariable Long id,
			@RequestBody StandTypeRequest request) {

		return ResponseEntity.ok(standTypeService.updateStandType(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStandType(@PathVariable Long id) {

		standTypeService.deleteStandType(id);

		return ResponseEntity.noContent().build();
	}
}