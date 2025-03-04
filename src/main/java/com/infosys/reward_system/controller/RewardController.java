package com.infosys.reward_system.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.reward_system.dto.RewardResponseDto;
import com.infosys.reward_system.exception.InvalidDateRangeException;
import com.infosys.reward_system.service.RewardService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class RewardController {
	private final RewardService rewardService;

	public RewardController(RewardService rewardService) {
		this.rewardService = rewardService;
	}

	@GetMapping("/rewards")
	public ResponseEntity<List<RewardResponseDto>> getAllCustomerRewards(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		log.info("Fetching rewards for all customers from {} to {}", startDate, endDate);

		if (isDateRangeInvalid(startDate, endDate)) {
			log.warn("Invalid date range provided: {} - {}", startDate, endDate);
			throw new InvalidDateRangeException(startDate, endDate);
		}

		return ResponseEntity.ok(rewardService.calculateAllCustomerRewards(startDate, endDate));
	}

	@GetMapping("/rewards/{customerId}")
	public ResponseEntity<RewardResponseDto> getCustomerRewards(@PathVariable int customerId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		log.info("Fetching rewards for customer {}: startDate {} - endDate {}", customerId, startDate, endDate);

		if (isDateRangeInvalid(startDate, endDate)) {
			log.warn("Invalid date range provided for customer {}: {} - {}", customerId, startDate, endDate);
			throw new InvalidDateRangeException(startDate, endDate);
		}

		return ResponseEntity.ok(rewardService.calculateCustomerRewards(customerId, startDate, endDate));

	}

	private boolean isDateRangeInvalid(LocalDate startDate, LocalDate endDate) {
		return startDate != null && endDate != null && startDate.isAfter(endDate);
	}

}