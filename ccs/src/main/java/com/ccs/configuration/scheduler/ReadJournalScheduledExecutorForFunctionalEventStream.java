package com.ccs.configuration.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccs.dao.FunctionalDao;
import com.ccs.entities.converter.CustomerConverter;
import com.ccs.entities.converter.EventConverter;
import com.ccs.readJournal.FunctionalEventJdbcReadJournal;
import com.ccs.readJournal.JdbcReadJournal;
import com.ccs.repository.Messages;
import com.ccs.repository.nosql.CustomerCommandRepository;
import com.ccs.repository.nosql.EventCommandRepository;
import com.ccs.util.EventNames;
import com.ccs.util.TimestampPropertyUtil;
import com.datastax.driver.core.utils.UUIDs;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.Offset;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;
     
@Component("readJournalScheduledExecutorForFunctionalStream")
public class ReadJournalScheduledExecutorForFunctionalEventStream {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadJournalScheduledExecutorForFunctionalEventStream.class);
     
    @Autowired
    FunctionalDao dao;
    
    @Autowired
	CustomerCommandRepository messagesCommandRepository;
    
    @Autowired
	EventCommandRepository eventCommandRepository;
     
    private JdbcReadJournal journal;
     
    private ActorSystem actorSystem;
    
    private static String streamName = "fx-stream";
 
    @PostConstruct
    public void postConstruct() {
        ReadJournalFxTask readJournalTask = new ReadJournalFxTask (streamName);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(readJournalTask, 5 , TimeUnit.SECONDS);
         
    }
     
     
 
    class ReadJournalFxTask implements Runnable {
        private String name;
 
        public ReadJournalFxTask(String name) {
            this.name = name;
            actorSystem = ActorSystem.create("ccs_fx");
            journal = new FunctionalEventJdbcReadJournal(actorSystem, null, dao);
        }
 
        public String getName() {
            return name;
        }
 
        @Override
        public void run() {
            String offset = TimestampPropertyUtil.readProcessedOffset(streamName);
            Source<EventEnvelope, NotUsed> source = journal.eventsByTag(name, Offset.timeBasedUUID(UUID.fromString(offset)));
            if (source != null) {
                source.runForeach(eventEnvelope -> {
                    Object event = eventEnvelope.event();
                    if (event instanceof Messages) {
                    	Messages messages= (Messages) event;
                    	processEvents(messages);
                    	TimestampPropertyUtil.updateProcessedOffset(streamName, UUIDs.startOf(messages.getAction_timestamp().getTime()).toString());
                    }
 
                }, ActorMaterializer.create(actorSystem));
            }
        }
        
        private void processEvents(Messages messages) {
    		if(EventNames.customerAdded.equals(messages.getSer_manifest())) {	
    			LOGGER.debug("Persisting entity to MongoDB: Starts");
    			Instant start = Instant.now();
    			messagesCommandRepository.createCustomer(CustomerConverter.convertToCustomer(messages));
    			Instant end = Instant.now();
    			LOGGER.info("Time taken to persist : " + Duration.between(start, end));
    			LOGGER.debug("Persisting entity to MongoDB: Ends");
    		}else if(EventNames.customerDetailsUpdated.equals(messages.getSer_manifest())
    				|| EventNames.customerAddressAdded.equals(messages.getSer_manifest())
    				|| EventNames.customerRelocated.equals(messages.getSer_manifest())) {
    			LOGGER.debug("Updating entity in MongoDB: Starts");
    			messagesCommandRepository.updateCustomer(CustomerConverter.convertToCustomer(messages));
    			LOGGER.debug("Updated entity in MongoDB: Ends");
    		}
    		LOGGER.debug("Persisting event to MongoDB: Starts");
    		eventCommandRepository.createEvent(EventConverter.convertToEvent(messages));
    	}
    }
}
		
	

