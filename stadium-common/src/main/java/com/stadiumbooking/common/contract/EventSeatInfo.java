package com.stadiumbooking.common.contract;

import java.math.BigDecimal;

public record EventSeatInfo(Long id, Long eventId, Long seatId, String standName, String rowNumber, Integer seatNumber,
		BigDecimal price) {
}
