package com.infosys.reward_system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the reward points for a transaction.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRewardDto {
	private int transactionId;
	private BigDecimal transactionAmount;
	private LocalDate transactionDate;
	private int transactionRewardPoints;

}
