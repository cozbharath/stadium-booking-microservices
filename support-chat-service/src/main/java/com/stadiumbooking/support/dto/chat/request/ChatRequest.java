package com.stadiumbooking.support.dto.chat.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {

	@NotBlank(message = "message must not be empty")
	private String message;

	// Optional: the client omits this on the first turn, then sends back whatever
	// ChatResponse.conversationId returned so the history can be tracked.
	private String conversationId;
}