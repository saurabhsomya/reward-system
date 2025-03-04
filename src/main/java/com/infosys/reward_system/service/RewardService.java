package com.infosys.reward_system.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.infosys.reward_system.dto.RewardResponseDto;
import com.infosys.reward_system.dto.TransactionRewardDto;
import com.infosys.reward_system.exception.CustomerNotFoundException;
import com.infosys.reward_system.model.Transaction;
import com.infosys.reward_system.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RewardService {

	private TransactionRepository transactionRepository;

	@Async
	public CompletableFuture<List<RewardResponseDto>> calculateAllCustomerRewardsAsync(LocalDate startDate,
			LocalDate endDate) {
		log.info("Calculating rewards for all customers...");

		List<Integer> distinctCustomerIds = transactionRepository.findDistinctCustomerIds();
		List<RewardResponseDto> allCustomerRewardsList = distinctCustomerIds.stream()
				.map(id -> calculateCustomerRewards(id, startDate, endDate)).collect(Collectors.toList());

		return CompletableFuture.completedFuture(allCustomerRewardsList);

	}

	@Async
	public CompletableFuture<RewardResponseDto> calculateCustomerRewardsAsync(int customerId, LocalDate startDate,
			LocalDate endDate) {
		return CompletableFuture.completedFuture(calculateCustomerRewards(customerId, startDate, endDate));
	}

	public RewardResponseDto calculateCustomerRewards(int customerId, LocalDate startDate, LocalDate endDate) {
		log.info("Calculating rewards for customer {}", customerId);

		List<Transaction> customerTransactions = getCustomerTransactions(customerId, startDate, endDate);

		if (customerTransactions.isEmpty()) {
			log.error("No transactions found for customer {}", customerId);
			throw new CustomerNotFoundException(customerId);
		}

		int totalRewardPoints = 0;
		String customerName = customerTransactions.get(0).getCustomerName();
		List<TransactionRewardDto> transactionRewardDtos = new ArrayList<>();
		Map<String, Integer> monthlyRewards = new HashMap<>();

		for (Transaction row : customerTransactions) { // populating momnthlyRewards map
			int rewardPoints = calculateRewardPoints(row.getAmount().intValue());
			totalRewardPoints += rewardPoints;

			String monthKey = row.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
			if (rewardPoints > 0) {
				monthlyRewards.put(monthKey, monthlyRewards.getOrDefault(monthKey, 0) + rewardPoints);
			}
			transactionRewardDtos.add(createTransactionRewardDto(row, rewardPoints));
		}

		log.debug("Rewards calculated: CustomerId={}, TotalPoints={}", customerId, totalRewardPoints);

		return createRewardResponseDto(customerId, customerName, totalRewardPoints, monthlyRewards,
				transactionRewardDtos);

	}

	private int calculateRewardPoints(int amount) {
		if (amount <= 50) {
			return 0;
		} else if (amount <= 100) {
			return amount - 50;
		} else {
			return (amount - 100) * 2 + 50;
		}
	}

	private TransactionRewardDto createTransactionRewardDto(Transaction transaction, int rewardPoints) {
		return TransactionRewardDto.builder().transactionId(transaction.getTransactionId())
				.transactionAmount(transaction.getAmount()).transactionDate(transaction.getTransactionDate())
				.transactionRewardPoints(rewardPoints).build();
	}

	private RewardResponseDto createRewardResponseDto(int customerId, String customerName, int totalRewardPoints,
			Map<String, Integer> monthlyRewards, List<TransactionRewardDto> transactionRewardDtos) {
		return RewardResponseDto.builder().customerId(customerId).customerName(customerName)
				.totalRewardPoints(totalRewardPoints).monthlyRewards(monthlyRewards).transactions(transactionRewardDtos)
				.build();
	}

	private List<Transaction> getCustomerTransactions(int customerId, LocalDate startDate, LocalDate endDate) {
		return (startDate == null || endDate == null) ? transactionRepository.findByCustomerId(customerId)
				: transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);
	}

}