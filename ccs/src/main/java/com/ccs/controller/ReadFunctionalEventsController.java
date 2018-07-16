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

import com.ccs.dao.FunctionalDao;
import com.ccs.domain.Customer;
import com.ccs.domain.Event;
import com.ccs.readJournal.FunctionalEventJdbcReadJournal;
import com.ccs.readJournal.JdbcReadJournal;
import com.ccs.repository.Messages;
import com.ccs.repository.nosql.CustomerSearchRepository;
import com.ccs.repository.nosql.EventSearchRepository;

import akka.actor.ActorSystem;


@RestController
@RequestMapping("ccs")
@CrossOrigin
public class ReadFunctionalEventsController {
	
	@Autowired
	FunctionalDao dao;
	
	@Autowired
	CustomerSearchRepository searchRepository;
	
    @Autowired
    EventSearchRepository eventSearchRepository;
	
	private JdbcReadJournal journal;
	
	private ActorSystem actorSystem;
	
	
	@RequestMapping(value = "getMessage/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Messages>> getEventsByPersistenceId(@PathVariable("id") String id) throws Exception {

		actorSystem = ActorSystem.create("ccs");
		journal = new FunctionalEventJdbcReadJournal(actorSystem, null, dao);
		List<Messages> messages = journal.getEventsByPersistenceIdAsMessages(id, 0, 100);

		return new ResponseEntity<List<Messages>>(messages, HttpStatus.OK);
	}
	
	@RequestMapping(value = "getJSONMessage/{id}", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getJSONEventByPersistenceId(@PathVariable("id") String id) throws Exception {
		JSONObject jsonMessage = null;
		Customer messages = searchRepository.getCustomerById(id);

		if(messages != null) {
			JSONParser parser = new JSONParser();
	        jsonMessage = (JSONObject) parser.parse(messages.getEventJSON());
	        System.out.println(jsonMessage);
		}
		return new ResponseEntity<JSONObject>(jsonMessage, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "getEventsJSONMessage/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<JSONObject>> getFunctionalJSONEventByPersistenceId(@PathVariable("id") String id) throws Exception {

		List<Event> events = eventSearchRepository.getEventById(id);
		List<JSONObject> result = new ArrayList<>();
		JSONParser parser = new JSONParser();
		for (Event event : events) {
			JSONObject jsonMessage = (JSONObject) parser.parse(event.getEventJSON());
			result.add(jsonMessage);
			System.out.println(jsonMessage);
		}
		return new ResponseEntity<List<JSONObject>>(result, HttpStatus.OK);
	}

}
