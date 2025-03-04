package com.infosys.reward_system.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the response containing the reward details for a customer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardResponseDto {
	private int customerId;
	private String customerName;
	private int totalRewardPoints;
	private Map<String, Integer> monthlyRewards;
	private List<TransactionRewardDto> transactions;
}
