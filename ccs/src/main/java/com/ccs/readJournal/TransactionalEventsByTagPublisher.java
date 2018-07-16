package com.ccs.readJournal;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import com.ccs.dao.TransactionalDao;
import com.ccs.repository.RelationEventJSON;
import com.datastax.driver.core.utils.UUIDs;

import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.Scheduler;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.Offset;
import akka.stream.actor.AbstractActorPublisher;
import akka.stream.actor.ActorPublisherMessage.Cancel;
import scala.concurrent.duration.FiniteDuration;

public class TransactionalEventsByTagPublisher extends AbstractActorPublisher<EventEnvelope> {

	private final TransactionalDao dao;

	private final String tag;

	private final String CONTINUE = "CONTINUE";
	private final int LIMIT = 100;
	private Timestamp currentOffset;
	private List<EventEnvelope> buf = new LinkedList<>();

	private Cancellable continueTask;

	public TransactionalEventsByTagPublisher(TransactionalDao dao, String tag, Timestamp offset, FiniteDuration refreshInterval) {
		this.dao = dao;
		this.tag = tag;
		this.currentOffset = offset;

		final Scheduler scheduler = getContext().getSystem().scheduler();
		this.continueTask = scheduler.schedule(refreshInterval, refreshInterval, getSelf(), CONTINUE,
				getContext().dispatcher(), getSelf());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().matchEquals(CONTINUE, (in) -> {
			query();
			deliverBuf();
		}).match(Cancel.class, (in) -> {
			getContext().stop(getSelf());
		}).build();
	}

	public static Props props(TransactionalDao dao, String tag, Timestamp offset, FiniteDuration refreshInterval) {
		return Props.create(TransactionalEventsByTagPublisher.class, dao, tag, offset, refreshInterval);
	}

	@Override
	public void postStop() {
		continueTask.cancel();
	}

	private void query() {
		if (buf.isEmpty()) {

			List<RelationEventJSON> messages = dao.getEventsByTag(tag, currentOffset, LIMIT);

			List<EventEnvelope> events = new LinkedList<>();
			for (RelationEventJSON message : messages) {
				events.add(new EventEnvelope(Offset.timeBasedUUID(UUIDs.startOf(message.getAction_timestamp().getTime())), message.getMessageId().getPersistenceId(),
						message.getMessageId().getSequenceNr(), message));
			}
			
			if (!messages.isEmpty()) {
				currentOffset = (messages.get(messages.size()-1).getAction_timestamp());
			}
			buf = events;

		}
	}

	private void deliverBuf() {
		while (totalDemand() > 0 && !buf.isEmpty())
			onNext(buf.remove(0));
	}
}
