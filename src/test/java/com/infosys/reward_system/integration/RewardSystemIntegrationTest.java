package com.infosys.reward_system.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RewardSystemIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	/**
	 * Test case: Fetching rewards for an existing customer
	 */
	@Test
	void testGetRewardsForExistingCustomer() throws Exception {
		mockMvc.perform(get("/api/rewards/{customerId}", 1).param("startDate", "2024-11-01")
				.param("endDate", "2025-01-31").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value(1)).andExpect(jsonPath("$.customerName").value("Saurabh"))
				.andExpect(jsonPath("$.totalRewardPoints").exists()).andExpect(jsonPath("$.monthlyRewards").isMap())
				.andExpect(jsonPath("$.transactions").isArray());
	}

	/**
	 * Test case: Fetching rewards for a non-existent customer
	 */
	@Test
	void testGetRewardsForNonExistentCustomer() throws Exception {
		MvcResult result = mockMvc
				.perform(get("/api/rewards/{customerId}", 999).param("startDate", "2024-11-01")
						.param("endDate", "2025-01-31").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()) // Expected 404
				.andReturn();

		System.out.println("Response: " + result.getResponse().getContentAsString());
	}

	/**
	 * Test case: Fetching rewards with an invalid date range
	 */
	@Test
	void testGetRewardsWithInvalidDateRange() throws Exception {
		mockMvc.perform(
				get("/api/rewards/{customerId}", 1).param("startDate", "2025-02-01").param("endDate", "2024-11-01") // Invalid:
																													// End
																													// date
																													// before
																													// start
																													// date
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(
						"Invalid date range: startDate (2025-02-01) must be before or equal to endDate (2024-11-01)"));
	}
}
