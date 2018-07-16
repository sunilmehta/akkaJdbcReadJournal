package com.ccs.entities.converter;

import com.ccs.domain.Relation;
import com.ccs.repository.RelationEventJSON;

public class RelationConverter {
	
	public static Relation convertToRelation(RelationEventJSON commandEntity) {
	return new Relation(commandEntity.getMessageId().getPersistenceId(), commandEntity.getEvent());
}}
