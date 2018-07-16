package com.ccs.repository.nosql;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.ccs.domain.Relation;
import com.mongodb.client.result.UpdateResult;

@Repository
public class RelationCommandRepository {

	@Autowired
	MongoTemplate mongoTemplate;

	@Transactional
	@Async
	public void createRelation(Relation relation) {
		mongoTemplate.save(relation);
	}

	@Transactional
	@Async
	public UpdateResult updateRelation(Relation relation) {
		return mongoTemplate.updateFirst(new Query(Criteria.where("customerId").is(relation.getPersistenceId())),
				Update.update("eventJSON", relation.getEventJSON()), Relation.class);
	}


}
