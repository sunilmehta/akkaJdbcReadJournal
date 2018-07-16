package com.ccs.readJournal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ccs.repository.Messages;
import com.ccs.repository.RelationEventJSON;
import com.typesafe.config.Config;

import akka.NotUsed;
import akka.actor.ExtendedActorSystem;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.Offset;
import akka.persistence.query.javadsl.CurrentEventsByPersistenceIdQuery;
import akka.persistence.query.javadsl.CurrentEventsByTagQuery;
import akka.persistence.query.javadsl.CurrentPersistenceIdsQuery;
import akka.persistence.query.javadsl.EventsByPersistenceIdQuery;
import akka.persistence.query.javadsl.EventsByTagQuery;
import akka.persistence.query.javadsl.PersistenceIdsQuery;
import akka.persistence.query.javadsl.ReadJournal;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.FiniteDuration;

public class JdbcReadJournal implements ReadJournal, PersistenceIdsQuery, CurrentPersistenceIdsQuery, EventsByPersistenceIdQuery, CurrentEventsByPersistenceIdQuery, EventsByTagQuery, CurrentEventsByTagQuery {


	public static JdbcReadJournal journal;
	
	protected static String identifier = "jdbc-read-journal";
	
	protected static FiniteDuration refreshInterval;
	
	public JdbcReadJournal() {
		
	}

    public JdbcReadJournal(ExtendedActorSystem system, Config config) {
    	refreshInterval = FiniteDuration.create(config.getDuration("refresh-interval", 
                TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
	}

	protected  final String Identifier()
    {
        return "jdbc-read-journal";
    }

	@Override
	public Source<EventEnvelope, NotUsed> eventsByTag(String tag, Offset offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Source<EventEnvelope, NotUsed> currentEventsByPersistenceId(String persistenceId, long fromSequenceNr,
			long toSequenceNr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Source<String, NotUsed> currentPersistenceIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Source<String, NotUsed> persistenceIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Source<EventEnvelope, NotUsed> currentEventsByTag(String tag, Offset offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Source<EventEnvelope, NotUsed> eventsByPersistenceId(String persistenceId, long fromSequenceNr,
			long toSequenceNr) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Messages> getEventsByPersistenceIdAsMessages(String persistenceId, long fromSequenceNr,
			long toSequenceNr) {

		return null;
	}
	
	public List<RelationEventJSON> getEventsByPersistenceId(String persistenceId, long fromSequenceNr,
			long toSequenceNr) {

		return null;
	}
	
	public long refreshInterval() {
    	return refreshInterval.length();
    }

	}
