package com.ccs.repository.nosql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.ccs.domain.Customer;

@Repository
public class CustomerSearchRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	public Customer getCustomerById(final String customerId) {
		return mongoTemplate.findOne(new Query(Criteria.where("customerId").is(customerId)), Customer.class);
	}

	public List<Customer> getEntities() {
		return mongoTemplate.findAll(Customer.class);
	}

	/**
	 * Gets the entities by entity type.
	 *
	 * @param entityTypeId the entity type id
	 * @return the entities by entity type
	 */
	public List<Customer> getEntitiesByCustomerType(final String entityTypeId) {
		return mongoTemplate.find(new Query(Criteria.where("entityTypeId").is(entityTypeId)),
				Customer.class);
	}
	
	/**
	 * Checks if the {@link CustomerType} with id: {@code entityTypeId} is updatable/deletable.
	 * <br/> The entity type is updatable or deletable if there exist not even one
	 * entity of this entity-type.
	 *
	 * @param entityTypeId the entity type id
	 * @return {@code true}, if the entity type is updatable.
	 */
	public boolean isUpdatable(final String entityTypeId) {
		final Query query = new Query(Criteria.where("entityTypeId").is(entityTypeId));
		query.limit(1);
		
		return CollectionUtils.isEmpty(mongoTemplate.find(query, Customer.class));
	}
}
