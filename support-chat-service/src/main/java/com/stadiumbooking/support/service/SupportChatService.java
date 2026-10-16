package com.stadiumbooking.support.service;

import com.stadiumbooking.support.dto.chat.request.ChatRequest;
import com.stadiumbooking.support.dto.chat.response.ChatResponse;

public interface SupportChatService {

	ChatResponse chat(ChatRequest request);
}