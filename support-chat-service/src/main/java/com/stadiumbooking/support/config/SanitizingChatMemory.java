package com.stadiumbooking.support.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;

/**
 * Wraps a ChatMemory and strips provider-specific metadata (such as Groq's
 * "reasoning_content" on gpt-oss models) from assistant replies before they are
 * stored. Without this, the stored reply gets replayed back to Groq on the next
 * turn and Groq rejects the whole request with a 400, because
 * "reasoning_content" is only valid in a response FROM the model, not in a
 * message sent back TO it.
 */
public class SanitizingChatMemory implements ChatMemory {

	private final ChatMemory delegate;

	public SanitizingChatMemory(ChatMemory delegate) {
		this.delegate = delegate;
	}

	@Override
	public void add(String conversationId, List<Message> messages) {

		List<Message> sanitized = messages.stream().map(this::sanitize).collect(Collectors.toList());

		delegate.add(conversationId, sanitized);
	}

	@Override
	public List<Message> get(String conversationId) {
		return delegate.get(conversationId);
	}

	@Override
	public void clear(String conversationId) {
		delegate.clear(conversationId);
	}

	private Message sanitize(Message message) {

		if (message instanceof AssistantMessage assistantMessage) {
			// Keep only the plain reply text - drops metadata (where
			// reasoning_content lives) and tool-call history.
			return new AssistantMessage(assistantMessage.getText());
		}

		return message;
	}
}