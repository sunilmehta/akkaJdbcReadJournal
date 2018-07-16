package com.ccs.entities.converter;


import com.ccs.domain.Customer;
import com.ccs.repository.Messages;


public class CustomerConverter {
	public static Customer convertToCustomer(Messages commandEntity) {
		return new Customer(commandEntity.getMessageId().getPersistenceId(), commandEntity.getEvent());
	}

}
