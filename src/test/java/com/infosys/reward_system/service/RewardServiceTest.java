package com.infosys.reward_system.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.infosys.reward_system.dto.RewardResponseDto;
import com.infosys.reward_system.exception.CustomerNotFoundException;
import com.infosys.reward_system.model.Transaction;
import com.infosys.reward_system.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

	@Mock
	private TransactionRepository transactionRepository;

	@InjectMocks
	private RewardService rewardService;

	private List<Transaction> sampleTransactions;

	@BeforeEach
	void setUp() {
		sampleTransactions = Arrays.asList(
				new Transaction(1, 101, "John Doe", BigDecimal.valueOf(120), LocalDate.of(2024, 1, 10)),
				new Transaction(2, 101, "John Doe", BigDecimal.valueOf(75), LocalDate.of(2024, 2, 15)),
				new Transaction(3, 101, "John Doe", BigDecimal.valueOf(30), LocalDate.of(2024, 3, 20)));
	}

	@Test
	void testCalculateCustomerRewards_ValidCustomer() {
		when(transactionRepository.findByCustomerIdAndTransactionDateBetween(anyInt(), any(), any()))
				.thenReturn(sampleTransactions);

		RewardResponseDto response = rewardService.calculateCustomerRewards(101, LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 3, 31));

		assertEquals(101, response.getCustomerId());
		assertEquals("John Doe", response.getCustomerName());
		assertEquals(90 + 25, response.getTotalRewardPoints());

		Map<String, Integer> expectedMonthlyRewards = new HashMap<>();
		expectedMonthlyRewards.put("2024-01", 90);
		expectedMonthlyRewards.put("2024-02", 25);

		assertEquals(expectedMonthlyRewards, response.getMonthlyRewards());
	}

	@Test
	void testCalculateCustomerRewards_CustomerNotFound() {
		when(transactionRepository.findByCustomerIdAndTransactionDateBetween(anyInt(), any(), any()))
				.thenReturn(Collections.emptyList());

		assertThrows(CustomerNotFoundException.class,
				() -> rewardService.calculateCustomerRewards(102, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31)));
	}

	@Test
	void testCalculateAllCustomerRewards() {
		when(transactionRepository.findDistinctCustomerIds()).thenReturn(Collections.singletonList(101));
		when(transactionRepository.findByCustomerIdAndTransactionDateBetween(anyInt(), any(), any()))
				.thenReturn(sampleTransactions);

		List<RewardResponseDto> responseList = rewardService.calculateAllCustomerRewards(LocalDate.of(2024, 1, 1),
				LocalDate.of(2024, 3, 31));

		assertFalse(responseList.isEmpty());
		assertEquals(1, responseList.size());
		RewardResponseDto response = responseList.get(0);

		assertEquals(101, response.getCustomerId());
		assertEquals("John Doe", response.getCustomerName());
	}
}
