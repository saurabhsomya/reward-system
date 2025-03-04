package com.infosys.reward_system.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.reward_system.dto.RewardResponseDto;
import com.infosys.reward_system.exception.CustomerNotFoundException;
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
	public CompletableFuture<ResponseEntity<List<RewardResponseDto>>> getAllCustomerRewards(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
			) {
		log.info("Fetching rewards for all customers from {} to {}", startDate, endDate);

		if (isDateRangeInvalid(startDate, endDate)) {
			log.warn("Invalid date range provided: {} - {}", startDate, endDate);
			throw new InvalidDateRangeException(startDate, endDate);
		}

		return rewardService.calculateAllCustomerRewardsAsync(startDate, endDate).thenApply(ResponseEntity::ok);
	}

	@GetMapping("/rewards/{customerId}")
	public CompletableFuture<ResponseEntity<RewardResponseDto>> getCustomerRewards(
			@PathVariable int customerId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
			) {
		log.info("Fetching rewards for customer {}: startDate {} - endDate {}", customerId, startDate, endDate);

		if (isDateRangeInvalid(startDate, endDate)) {
			log.warn("Invalid date range provided for customer {}: {} - {}", customerId, startDate, endDate);
			throw new InvalidDateRangeException(startDate, endDate);
		}

		return rewardService.calculateCustomerRewardsAsync(customerId, startDate, endDate)
				.thenApply(ResponseEntity::ok)
				.exceptionally(ex -> {
            if (ex.getCause() instanceof CustomerNotFoundException) {
                throw (CustomerNotFoundException) ex.getCause();
            }
            throw new RuntimeException(ex);
        });

	}

	private boolean isDateRangeInvalid(LocalDate startDate, LocalDate endDate) {

		return startDate != null && endDate != null && startDate.isAfter(endDate);

	}

}