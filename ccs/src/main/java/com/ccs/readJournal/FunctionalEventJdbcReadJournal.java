package com.ccs.readJournal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccs.dao.FunctionalDao;
import com.ccs.repository.Messages;
import com.ccs.util.UUIDToTimestamp;
import com.typesafe.config.Config;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.Offset;
import akka.persistence.query.PersistenceQuery;
import akka.persistence.query.TimeBasedUUID;
import akka.stream.javadsl.Source;

@Component("functional event read journal")
public class FunctionalEventJdbcReadJournal  extends JdbcReadJournal {
	
	@Autowired
	private  FunctionalDao dao;
	
	private FunctionalEventJdbcReadJournal()
    {
        journal = this;
    }

    static 
    {
        new FunctionalEventJdbcReadJournal();
    }
    
	public FunctionalEventJdbcReadJournal(ActorSystem system, Config config, FunctionalDao dao) {
		
		journal = PersistenceQuery.get(system).getReadJournalFor(journal.getClass(),
				identifier);
		this.dao = dao;
	}
	
	
	@Override
	public Source<EventEnvelope, NotUsed> eventsByPersistenceId(String persistenceId, long fromSequenceNr,
			long toSequenceNr) {
	
		Source<EventEnvelope, NotUsed> result = null;;
		List<Messages>  messages = dao.getEventsByPersistenceID(persistenceId, fromSequenceNr, toSequenceNr);
		
		if (messages.isEmpty()) {
			result = null;
		} else {
			List<EventEnvelope> events = new ArrayList<>();
			for (Messages message: messages) {
				events.add(new EventEnvelope(null, persistenceId, toSequenceNr, message));
			}
			result = Source.from(events);
		}
		
		return result;
	}
	
	@Override
	public Source<EventEnvelope, NotUsed> eventsByTag(String tag, Offset offset) {
		
		if (offset instanceof TimeBasedUUID) {
			TimeBasedUUID timebasedOffset = (TimeBasedUUID) offset;
			final Props props = FunctionalEventsByTagPublisher.props(dao , tag, UUIDToTimestamp.getTimestamp(timebasedOffset.value().toString()), refreshInterval);
			return Source.<EventEnvelope> actorPublisher(props).mapMaterializedValue(m -> NotUsed.getInstance());
		}	else
			throw new IllegalArgumentException(
					"ReadJournal does not support " + offset.getClass().getName() + " offsets");
	}
	
	@Override
	public Source<EventEnvelope, NotUsed> currentEventsByPersistenceId(String persistenceId, long fromSequenceNr,
			long toSequenceNr) {
		
		List<Messages> messages = getEventsByPersistenceIdAsMessages(persistenceId, fromSequenceNr, toSequenceNr);
		List<EventEnvelope> events = new ArrayList<>();
		for (Messages message : messages) {
			events.add(new EventEnvelope(null, persistenceId, toSequenceNr, message));
		}
		return Source.from(events);
		
	}
	
	@Override
	public List<Messages> getEventsByPersistenceIdAsMessages(String persistenceId, long fromSequenceNr,
			long toSequenceNr) {

		return dao.getEventsByPersistenceID(persistenceId, fromSequenceNr, toSequenceNr);
	}
}
