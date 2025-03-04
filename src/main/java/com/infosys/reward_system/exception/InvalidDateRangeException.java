package com.infosys.reward_system.exception;

import java.time.LocalDate;

public class InvalidDateRangeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidDateRangeException(LocalDate startDate, LocalDate endDate) {
		super(String.format("Invalid date range: startDate (%s) must be before or equal to endDate (%s)", startDate,
				endDate));
	}
}
