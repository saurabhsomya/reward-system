package com.infosys.reward_system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.infosys.reward_system.dto.RewardResponseDto;
import com.infosys.reward_system.exception.GlobalExceptionHandler;
import com.infosys.reward_system.service.RewardService;

@ExtendWith(MockitoExtension.class)
public class RewardControllerTest {

	@Mock
	private RewardService rewardService;

	@InjectMocks
	private RewardController rewardController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(rewardController).setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	void testGetAllCustomerRewards_ValidRequest() throws Exception {
		LocalDate startDate = LocalDate.of(2024, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 12, 30);

		List<RewardResponseDto> mockResponse = Collections.singletonList(
				new RewardResponseDto(1, "John Doe", 150, Collections.emptyMap(), Collections.emptyList()));

		when(rewardService.calculateAllCustomerRewards(startDate, endDate)).thenReturn(mockResponse);

		mockMvc.perform(get("/api/rewards").param("startDate", "2024-01-01").param("endDate", "2025-12-30")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1)).andExpect(jsonPath("$[0].customerId").value(1))
				.andExpect(jsonPath("$[0].customerName").value("John Doe"))
				.andExpect(jsonPath("$[0].totalRewardPoints").value(150));

		verify(rewardService, times(1)).calculateAllCustomerRewards(startDate, endDate);
	}

	@Test
	void testGetAllCustomerRewards_InvalidDateRange() throws Exception {
		mockMvc.perform(get("/api/rewards").param("startDate", "2024-03-31").param("endDate", "2024-01-01")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.error").value("Bad Request"))
				.andExpect(jsonPath("$.message").value(
						"Invalid date range: startDate (2024-03-31) must be before or equal to endDate (2024-01-01)"));

		verify(rewardService, never()).calculateAllCustomerRewards(any(), any());
	}

	@Test
	void testGetCustomerRewards_ValidRequest() throws Exception {
		int customerId = 1;
		LocalDate startDate = LocalDate.of(2024, 1, 1);
		LocalDate endDate = LocalDate.of(2024, 3, 30);

		RewardResponseDto mockResponse = new RewardResponseDto(1, "John Doe", 150, Collections.emptyMap(),
				Collections.emptyList());

		when(rewardService.calculateCustomerRewards(customerId, startDate, endDate)).thenReturn(mockResponse);

		mockMvc.perform(get("/api/rewards/1").param("startDate", "2024-01-01").param("endDate", "2024-03-30")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value(1)).andExpect(jsonPath("$.customerName").value("John Doe"))
				.andExpect(jsonPath("$.totalRewardPoints").value(150));

		verify(rewardService, times(1)).calculateCustomerRewards(customerId, startDate, endDate);
	}

	@Test
	void testGetCustomerRewards_InvalidDateRange() throws Exception {
		mockMvc.perform(get("/api/rewards/1").param("startDate", "2024-03-31").param("endDate", "2024-01-01"))
				.andExpect(status().isBadRequest());

		verify(rewardService, never()).calculateCustomerRewards(anyInt(), any(), any());
	}

}
