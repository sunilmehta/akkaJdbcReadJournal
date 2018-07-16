package com.ccs.configuration.scheduler;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccs.dao.TransactionalDao;
import com.ccs.entities.converter.RelationConverter;
import com.ccs.readJournal.JdbcReadJournal;
import com.ccs.readJournal.TransactionalEventJdbcReadJournal;
import com.ccs.repository.RelationEventJSON;
import com.ccs.repository.nosql.RelationCommandRepository;
import com.ccs.util.EventNames;
import com.ccs.util.TimestampPropertyUtil;
import com.datastax.driver.core.utils.UUIDs;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.Offset;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;

@Component("readJournalScheduledExecutorForTransationEventStream")
public class ReadJournalScheduledExecutorForTransationEventStream {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReadJournalScheduledExecutorForFunctionalEventStream.class);

	@Autowired
	TransactionalDao dao;

	@Autowired
	private RelationCommandRepository relationCommandRepository;

	private JdbcReadJournal journal;

	private ActorSystem actorSystem;

	private static String streamName = "db-tx-stream";

	@PostConstruct
	public void postConstruct() {
		ReadJournalTxTask readJournalTask = new ReadJournalTxTask(streamName);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.schedule(readJournalTask, 5, TimeUnit.SECONDS);

	}

	class ReadJournalTxTask implements Runnable {
		private String name;

		public ReadJournalTxTask(String name) {
			this.name = name;
			actorSystem = ActorSystem.create("ccs_tx");
			journal = new TransactionalEventJdbcReadJournal(actorSystem, null, dao);
		}

		public String getName() {
			return name;
		}

		@Override
		public void run() {
			String offset = TimestampPropertyUtil.readProcessedOffset(streamName);
			Source<EventEnvelope, NotUsed> source = journal.eventsByTag(name,
					Offset.timeBasedUUID(UUID.fromString(offset)));
			if (source != null) {
				source.runForeach(eventEnvelope -> {
					Object event = eventEnvelope.event();
					if (event instanceof RelationEventJSON) {
						RelationEventJSON messages = (RelationEventJSON) event;
						processEvents(messages);
						TimestampPropertyUtil.updateProcessedOffset(streamName,
								UUIDs.startOf(messages.getAction_timestamp().getTime()).toString());
					}

				}, ActorMaterializer.create(actorSystem));
			}
		}

		private void processEvents(RelationEventJSON messages) {
			if (EventNames.customerAdded.equals(messages.getSer_manifest())) {
				LOGGER.debug("Persisting relation to MongoDB : Starts");
				relationCommandRepository.createRelation(RelationConverter.convertToRelation(messages));
				LOGGER.debug("Persisting entity to MongoDB: Ends");
			} else if (EventNames.customerDetailsUpdated.equals(messages.getSer_manifest())
    				|| EventNames.customerAddressAdded.equals(messages.getSer_manifest())
    				|| EventNames.customerRelocated.equals(messages.getSer_manifest())) {
				LOGGER.debug("Updating entity in MongoDB: Starts");
				relationCommandRepository.updateRelation(RelationConverter.convertToRelation(messages));
				LOGGER.debug("Updated entity in MongoDB: Ends");
			}
		}
	}
}
	
