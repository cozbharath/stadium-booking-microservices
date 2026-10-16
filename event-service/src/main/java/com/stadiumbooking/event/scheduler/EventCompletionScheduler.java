package com.stadiumbooking.event.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stadiumbooking.event.service.EventService;

import lombok.RequiredArgsConstructor;

/** The "complete past events" part of the old ExpiryScheduler. */
@Component
@RequiredArgsConstructor
public class EventCompletionScheduler {

	private final EventService eventService;

	@Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
	public void completePastEvents() {
		eventService.completePastEvents();
	}
}