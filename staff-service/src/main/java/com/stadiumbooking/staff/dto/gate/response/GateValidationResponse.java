package com.stadiumbooking.staff.dto.gate.response;

import com.stadiumbooking.common.dto.TicketResponse;

/**
 * What the gate scanner shows: green (valid = true) or red (valid = false) plus
 * the reason.
 */
public record GateValidationResponse(boolean valid, String message, TicketResponse ticket) {
}