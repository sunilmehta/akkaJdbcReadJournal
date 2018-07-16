package com.ccs.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ccs.dao.TransactionalDao;
import com.ccs.domain.Customer;
import com.ccs.domain.Event;
import com.ccs.domain.Relation;
import com.ccs.readJournal.JdbcReadJournal;
import com.ccs.readJournal.TransactionalEventJdbcReadJournal;
import com.ccs.repository.RelationEventJSON;
import com.ccs.repository.nosql.EventSearchRepository;
import com.ccs.repository.nosql.RelationSearchRepository;

import akka.actor.ActorSystem;


@RestController
@RequestMapping("ccs_tx")
@CrossOrigin
public class TransactionalReadRelationEventController {
	
	@Autowired
	TransactionalDao dao;

	@Autowired
	RelationSearchRepository relationSearchRepository;
  
	private JdbcReadJournal journal;
	
	private ActorSystem actorSystem;
	
	
	@RequestMapping(value = "getMessage/{id}", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getEventsByPersistenceId(@PathVariable("id") String id) throws Exception {

		actorSystem = ActorSystem.create("ccs");
		journal = new TransactionalEventJdbcReadJournal(actorSystem, null, dao);
		List<RelationEventJSON> messages = journal.getEventsByPersistenceId(id, 0, 100);

		JSONParser parser = new JSONParser();
        JSONObject jsonMessage = (JSONObject) parser.parse(messages.get(0).getEvent());
        System.out.println(jsonMessage);
		return new ResponseEntity<JSONObject>(jsonMessage, HttpStatus.OK);
	}

	@RequestMapping(value = "getTxEventsJSONMessage/{id}", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getFunctionalJSONEventByPersistenceId(@PathVariable("id") String id) throws Exception {

		JSONObject jsonMessage = null;
		Relation messages = relationSearchRepository.getRelationById(id);

		if(messages != null) {
			JSONParser parser = new JSONParser();
	        jsonMessage = (JSONObject) parser.parse(messages.getEventJSON());
	        System.out.println(jsonMessage);
		}
		return new ResponseEntity<JSONObject>(jsonMessage, HttpStatus.OK);
	}
}
