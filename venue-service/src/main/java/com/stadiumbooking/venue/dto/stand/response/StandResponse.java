package com.stadiumbooking.venue.dto.stand.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StandResponse {

	private Long id;

	private String name;

	private Long stadiumId;

	private String stadiumName;

	private Long standTypeId;

	private String standTypeName;
}