package com.stadiumbooking.common.contract;

import java.math.BigDecimal;

public record InternalSeatData(Long bookingSeatId, String standName, String rowNumber, Integer seatNumber,
		BigDecimal price) {
}
