package com.stadiumbooking.venue.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Stands")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Stand {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@ManyToOne
	@JoinColumn(name = "stadium_id", nullable = false)
	private Stadium stadium;

	@ManyToOne
	@JoinColumn(name = "stand_type_id", nullable = false)
	private StandType standType;

	@OneToMany(mappedBy = "stand")
	private List<Seat> seats;
}