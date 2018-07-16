package com.ccs.repository.nosql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.ccs.domain.Customer;
import com.ccs.domain.Event;

@Repository
public class EventSearchRepository {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * Gets the entities by entity type.
	 *
	 * @param entityTypeId the entity type id
	 * @return the entities by entity type
	 */
	public List<Event> getEventById(final String eventId) {
		return mongoTemplate.find(new Query(Criteria.where("persistenceId").is(eventId)),
				Event.class);
	}
	
	public List<Event> getEvents() {
		return mongoTemplate.findAll(Event.class);
	}
	

}
