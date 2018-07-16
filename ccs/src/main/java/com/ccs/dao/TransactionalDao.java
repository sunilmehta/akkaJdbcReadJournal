package com.ccs.dao;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ccs.repository.RelationEventJSON;

@Repository
public class TransactionalDao {

	@Autowired
	EntityManager entityManager;


	public List<RelationEventJSON> getEventsByPersistenceID(String persistenceId, long fromSequenceNr, long toSequenceNr) {
		List<RelationEventJSON> messages = null;
		TypedQuery<RelationEventJSON> query = entityManager.createNamedQuery(
				"relationEventJSON.findByPersitenceId", RelationEventJSON.class);
		query.setParameter("persistenceId", persistenceId);
		query.setParameter("fromSequenceNr", fromSequenceNr);
		query.setParameter("toSequenceNr", toSequenceNr);
		messages = query.getResultList();
		
		return messages;
	}
	
	@SuppressWarnings("unchecked")
	//uses native query for progress
	public List<RelationEventJSON> getEventsByTag(String tag, Timestamp offset, long rownum) {
		List<RelationEventJSON> messages = null;
		Query query = entityManager.createNamedQuery(
				"relationEventJSON.findByTagWithoutLimit");
		query.setParameter("offset", offset);
		query.setParameter("tag", "%" + tag + "%" );
		//query.setParameter("rownum", rownum);
		messages = query.getResultList();
		
		return messages;
	}

}
