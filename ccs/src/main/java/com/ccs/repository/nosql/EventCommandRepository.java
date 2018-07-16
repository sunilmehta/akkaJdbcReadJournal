package com.ccs.repository.nosql;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.ccs.domain.Event;

@Repository
public class EventCommandRepository {

	@Autowired
	MongoTemplate mongoTemplate;

	@Transactional
	@Async
	public void createEvent(Event event) {
		mongoTemplate.save(event);
	}

}
