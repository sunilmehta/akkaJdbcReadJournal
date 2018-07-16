package com.ccs.domain;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Event implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	
	private String persistenceId;
	private Long sequenceNr;
	private String serManifest;
	private String eventJSON;

	public Event() {
	}

	public Event(String persistenceId, Long sequenceNr, String serManifest, String eventJSON) {
		this.persistenceId =persistenceId;
		this.eventJSON = eventJSON;
		this.sequenceNr = sequenceNr;
		this.serManifest = serManifest;
	}

	

	public String getPersistenceId() {
		return this.persistenceId;
	}

	public void setPersistenceId(String persistenceId) {
		this.persistenceId = persistenceId;
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
		return persistenceId.equals(((Event) obj).persistenceId) 
				&& sequenceNr.equals(((Event) obj).sequenceNr);
	}

	public Long getSequenceNr() {
		return sequenceNr;
	}

	public void setSequenceNr(Long sequenceNr) {
		this.sequenceNr = sequenceNr;
	}

	public String getSerManifest() {
		return serManifest;
	}

	public void setSerManifest(String serManifest) {
		this.serManifest = serManifest;
	}

	@Override
	public int hashCode() {
		return persistenceId.hashCode();
	}}
