package com.stadiumbooking.support.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stadiumbooking.support.dto.chat.request.ChatRequest;
import com.stadiumbooking.support.dto.chat.response.ChatResponse;
import com.stadiumbooking.support.service.SupportChatService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportChatController {

	private final SupportChatService supportChatService;

	@PostMapping("/chat")
	public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
		return supportChatService.chat(request);
	}
}