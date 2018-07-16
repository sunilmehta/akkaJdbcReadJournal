package com.ccs.readJournal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ccs.dao.TransactionalDao;
import com.ccs.repository.RelationEventJSON;
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

@Component("transactional event read journal")
public class TransactionalEventJdbcReadJournal  extends JdbcReadJournal {
	
	
	private  TransactionalDao dao;
	
	private TransactionalEventJdbcReadJournal()
    {
        journal = this;
    }

    static 
    {
        new TransactionalEventJdbcReadJournal();
    }
	
   		
	public TransactionalEventJdbcReadJournal(ActorSystem system, Config config, TransactionalDao dao) {
		journal = PersistenceQuery.get(system).getReadJournalFor(journal.getClass(),
				identifier);
		this.dao = dao;
	}
	
	@Override
	public Source<EventEnvelope, NotUsed> eventsByPersistenceId(String persistenceId, long fromSequenceNr,
			long toSequenceNr) {
	
		Source<EventEnvelope, NotUsed> result = null;;
		List<RelationEventJSON>  messages = dao.getEventsByPersistenceID(persistenceId, fromSequenceNr, toSequenceNr);
		
		if (messages.isEmpty()) {
			result = null;
		} else {
			List<EventEnvelope> events = new ArrayList<>();
			for (RelationEventJSON message: messages) {
				//result  = Source.repeat(new EventEnvelope(null, persistenceId, toSequenceNr, message));
				events.add(new EventEnvelope(null, persistenceId, toSequenceNr, message));
			}
			result = Source.from(events);
		}
		
		return result;

	}
	
	@Override
	public List<RelationEventJSON> getEventsByPersistenceId(String persistenceId, long fromSequenceNr,
			long toSequenceNr) {

		return dao.getEventsByPersistenceID(persistenceId, fromSequenceNr, toSequenceNr);
	}
	
	@Override
	public Source<EventEnvelope, NotUsed> eventsByTag(String tag, Offset offset) {
		
		if (offset instanceof TimeBasedUUID) {
			TimeBasedUUID timebasedOffset = (TimeBasedUUID) offset;
			final Props props = TransactionalEventsByTagPublisher.props(dao , tag, UUIDToTimestamp.getTimestamp(timebasedOffset.value().toString()), refreshInterval);
			return Source.<EventEnvelope> actorPublisher(props).mapMaterializedValue(m -> NotUsed.getInstance());
		}	else
			throw new IllegalArgumentException(
					"ReadJournal does not support " + offset.getClass().getName() + " offsets");
	}
}
