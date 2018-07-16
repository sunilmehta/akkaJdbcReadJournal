package com.ccs.dao;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ccs.repository.Messages;

@Repository
public class FunctionalDao {

	@Autowired
	EntityManager entityManager;


	public List<Messages> getEventsByPersistenceID(String persistenceId, long fromSequenceNr, long toSequenceNr) {
		List<Messages> messages = null;
		TypedQuery<Messages>  query = entityManager.createNamedQuery(
				"messages.findByPersitenceId", Messages.class);
		query.setParameter("persistenceId", persistenceId);
		query.setParameter("fromSequenceNr", fromSequenceNr);
		query.setParameter("toSequenceNr", toSequenceNr);
		messages = query.getResultList();
		
		return messages;
	}
	
	@SuppressWarnings("unchecked")
	//uses native query for progress
	public List<Messages> getEventsByTag(String tag, Timestamp offset, long rownum) {
		List<Messages> messages = null;
		Query query = entityManager.createNamedQuery(
				"messages.findByTagWithoutLimit");
		query.setParameter("offset", offset);
		query.setParameter("tag", "%" + tag + "%" );
		//query.setParameter("rownum", rownum);
		messages = query.getResultList();
		
		return messages;
	}
	
}
