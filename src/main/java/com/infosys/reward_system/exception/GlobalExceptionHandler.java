package com.infosys.reward_system.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleCustomerNotFound(CustomerNotFoundException ex) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
	}

	@ExceptionHandler(InvalidDateRangeException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidDateRange(InvalidDateRangeException ex) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
				"An unexpected error occurred: " + ex.getMessage());
	}

	private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
		Map<String, Object> errorResponse = new LinkedHashMap<>();
		errorResponse.put("timestamp", LocalDateTime.now());
		errorResponse.put("status", status.value());
		errorResponse.put("error", error);
		errorResponse.put("message", message);
		return new ResponseEntity<>(errorResponse, status);
	}
}
