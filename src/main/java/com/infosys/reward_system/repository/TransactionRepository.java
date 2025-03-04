package com.infosys.reward_system.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.reward_system.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
	List<Transaction> findByCustomerId(int customerId);

	List<Transaction> findByCustomerIdAndTransactionDateBetween(int customerId, LocalDate startDate, LocalDate endDate);

	@Query("SELECT DISTINCT t.customerId FROM Transaction t")
	List<Integer> findDistinctCustomerIds();

}