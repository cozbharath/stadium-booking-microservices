package com.stadiumbooking.venue.dto.stadium.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StadiumRequest {

	private String name;

	private String location;

	private Integer capacity;
}