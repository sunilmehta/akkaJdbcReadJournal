package com.ccs.domain;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Relation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private String customerId;
	private String eventJSON;

	public Relation() {
	}

	public Relation(String persistenceId, String eventJSON) {
		this.customerId =persistenceId;
		this.eventJSON = eventJSON;
	}

	

	public String getPersistenceId() {
		return this.customerId;
	}

	public void setPersistenceId(String persistenceId) {
		this.customerId = persistenceId;
	}

	public String getEventJSON() {
		return this.eventJSON;
	}

	public void setEventJSON(String eventJSON) {
		this.eventJSON = eventJSON;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(Object obj) {
		return customerId.equals(((Relation) obj).customerId);
	}

	@Override
	public int hashCode() {
		return customerId.hashCode();
	}
}
