package com.stadiumbooking.venue.dto.stand.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StandRequest {

	private String name;

	private Long stadiumId;

	private Long standTypeId;
}