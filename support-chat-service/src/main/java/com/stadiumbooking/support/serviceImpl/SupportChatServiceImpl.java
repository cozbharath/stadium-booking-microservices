package com.stadiumbooking.support.serviceImpl;

import java.util.Locale;
import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import com.stadiumbooking.support.dto.chat.request.ChatRequest;
import com.stadiumbooking.support.dto.chat.response.ChatResponse;
import com.stadiumbooking.support.service.SupportChatService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupportChatServiceImpl implements SupportChatService {

	private final ChatClient chatClient;

	@Override
    public ChatResponse chat(ChatRequest request) {

        String conversationId =
                request.getConversationId() != null
                        && !request.getConversationId().isBlank()
                ? request.getConversationId()
                : UUID.randomUUID().toString();

        String message = request.getMessage() == null
                ? ""
                : request.getMessage().trim();

        String normalized = message
                .toLowerCase(Locale.ROOT)
                .replaceAll("[!?. ,]+$", "")
                .trim();

        // Handle greetings without calling Ollama or booking tools.
        if (normalized.matches(
                "^(hi+|hy+|hyy+|hey+|hello+|good morning|good afternoon|good evening)$")) {

            String reply;

            if (normalized.equals("good morning")) {
                reply = "Good morning! How can I help you with your bookings, tickets, or payments today?";
            } else if (normalized.equals("good afternoon")) {
                reply = "Good afternoon! How can I help you with your bookings, tickets, or payments today?";
            } else if (normalized.equals("good evening")) {
                reply = "Good evening! How can I help you with your bookings, tickets, or payments today?";
            } else {
                reply = "Hi! Welcome to YourTurn support. I can help with bookings, tickets, payments, cancellations, and platform questions.";
            }

            return new ChatResponse(reply, conversationId);
        }

        // Send other questions to Spring AI / Ollama.
        String reply = chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID,
                        conversationId))
                .call()
                .content();

        if (reply == null || reply.isBlank()) {
            reply = "Sorry, I couldn't generate a response. Please try again.";
        }

        return new ChatResponse(reply, conversationId);
    }
}