package com.ccs.entities.converter;

import com.ccs.domain.Event;
import com.ccs.repository.Messages;

public class EventConverter {
	
	public static Event convertToEvent(Messages commandEntity) {
		return new Event(commandEntity.getMessageId().getPersistenceId(), commandEntity.getMessageId().getSequenceNr(),
				commandEntity.getSer_manifest(), commandEntity.getEvent());
	}

}
