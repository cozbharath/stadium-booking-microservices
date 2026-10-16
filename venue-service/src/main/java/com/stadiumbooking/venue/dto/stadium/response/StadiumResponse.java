package com.stadiumbooking.venue.dto.stadium.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StadiumResponse {

	private Long id;

	private String name;

	private String location;

	private Integer capacity;
}