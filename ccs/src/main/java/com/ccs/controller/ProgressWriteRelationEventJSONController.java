package com.ccs.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.ccs.readJournal.JdbcReadJournal;
import com.ccs.readJournal.TransactionalEventJdbcReadJournal;
import com.ccs.repository.RelationEventJSON;
import com.ccs.repository.nosql.CustomerCommandRepository;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.persistence.query.EventEnvelope;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;


@RestController
@RequestMapping("test-ccs")
@CrossOrigin
public class ProgressWriteRelationEventJSONController {
	
	@Autowired
	TransactionalDao dao;
	
	@Autowired
	CustomerCommandRepository commandRepository;
	
	private JdbcReadJournal journal;
	
	private ActorSystem actorSystem;
	
	@RequestMapping(value = "getMessageAsStream/{id}", method = RequestMethod.GET)
	public ResponseEntity<RelationEventJSON> getEventsByPersistenceIdNew(@PathVariable("id") String id) throws Exception {
		
	actorSystem = ActorSystem.create("ccs");
	journal = new TransactionalEventJdbcReadJournal(actorSystem, null, dao);
	List<RelationEventJSON> messages = new ArrayList<>();
	messages.add(new RelationEventJSON());
	Source<EventEnvelope, NotUsed>  source = journal.eventsByPersistenceId(id,0,100);
	source.runForeach(eventEnvelope -> {
		Object event = eventEnvelope.event();
		if (event instanceof RelationEventJSON) {
			System.out.println(event); 
		}
	}, ActorMaterializer.create(actorSystem));
	
	
	 return new ResponseEntity<RelationEventJSON>(messages.get(0), HttpStatus.OK);
	}
	
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
	
	@RequestMapping(value = "saveTest", method = RequestMethod.GET)
	public ResponseEntity<String> createCustomer() throws Exception {

		commandRepository.createCustomer(new Customer("Customer1", "json"));
		
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

}
