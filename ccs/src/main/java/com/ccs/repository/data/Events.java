package com.ccs.repository.data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.ccs.repository.MessageIdentity;

@Entity
@Table(name = "events")

@NamedQueries({
	  @NamedQuery(name="events.findByPersitenceId",
	              query = "from Events where messageId.persistenceId = :persistenceId and messageId.sequenceNr between :fromSequenceNr and :toSequenceNr order by messageId.sequenceNr")
	})
public class Events {
	
	@EmbeddedId
	private MessageIdentity messageId;
	
	@Column(name = "EVENT")
	private String event;
	
	@Column(name = "SER_MANIFEST")
	private String ser_manifest;
	
	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getSer_manifest() {
		return ser_manifest;
	}

	public void setSer_manifest(String ser_manifest) {
		this.ser_manifest = ser_manifest;
	}

}
