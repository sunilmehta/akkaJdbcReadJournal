package com.ccs.repository.nosql;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.ccs.domain.Customer;
import com.mongodb.client.result.UpdateResult;

@Repository
public class CustomerCommandRepository {

	@Autowired
	MongoTemplate mongoTemplate;

	@Transactional
	@Async
	public void createCustomer(Customer customer) {
		mongoTemplate.save(customer);
	}

	@Transactional
	@Async
	public UpdateResult updateCustomer(Customer customer) {
		// Customer dbEn = mongoTemplate.findOne(new
		// Query(Criteria.where("customerId").is(customer.getEntityId())),
		// Customer.class);
		return mongoTemplate.updateFirst(new Query(Criteria.where("customerId").is(customer.getPersistenceId())),
				Update.update("eventJSON", customer.getEventJSON()), Customer.class);
	}

	@Transactional
	@Async
	public void deleteEntity(String customerId) {
		mongoTemplate.remove(new Query(Criteria.where("customerId").is(customerId)), Customer.class);
	}

}
