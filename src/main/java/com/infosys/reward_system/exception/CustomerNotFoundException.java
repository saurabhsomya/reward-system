package com.infosys.reward_system.exception;

public class CustomerNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CustomerNotFoundException(int customerId) {
        super(String.format("Customer with ID %d not found.", customerId));
    }
}
