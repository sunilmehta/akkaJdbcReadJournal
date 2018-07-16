package com.ccs.repository.nosql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.ccs.domain.Customer;
import com.ccs.domain.Event;
import com.ccs.domain.Relation;

@Repository
public class RelationSearchRepository {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * Gets the entities by entity type.
	 *
	 * @param entityTypeId the entity type id
	 * @return the entities by entity type
	 */
	public Relation getRelationById(final String relationId) {
		return mongoTemplate.findOne(new Query(Criteria.where("customerId").is(relationId)), Relation.class);
	}
	
	

}
