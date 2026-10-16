package com.stadiumbooking.venue.dto.standtype.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StandTypeResponse {

	private Long id;

	private String name;

	private String description;
}