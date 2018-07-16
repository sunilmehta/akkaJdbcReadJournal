package com.ccs.repository;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "TX_STREAM" , schema="PUB")
@NamedQueries({
	  @NamedQuery(name="relationEventJSON.findByPersitenceId",
	              query = "from RelationEventJSON where messageId.persistenceId = :persistenceId and messageId.sequenceNr between :fromSequenceNr and :toSequenceNr"),
	  @NamedQuery(name="relationEventJSON.findBySerManifest",
	              query = "from RelationEventJSON where ser_manifest in (:ser_manifest)"),
	  @NamedQuery(name="relationEventJSON.findByTagWithoutLimit",
      query = "from RelationEventJSON where action_timestamp > :offset and tags like :tag order by action_timestamp")
	})
@NamedNativeQueries( {
	@NamedNativeQuery(name="relationEventJSON.findByTag",
			query = "select TOP :rownum  * from REL_EVNT_JSON where action_timestamp > :offset and tags like :tag order by action_timestamp ")
})
public class RelationEventJSON {
	
	
	
	public RelationEventJSON() {
		super();
	}

	@EmbeddedId
	private MessageIdentity messageId;
	
	@Column(name = "ACTION_TIMESTAMP")
	private Timestamp action_timestamp;
	
	@Column(name = "EVENT")
	private String event;
	
	@Column(name = "SER_MANIFEST")
	private String ser_manifest;
	
	@Column(name = "TAGS")
	private String tags;
	
	@Column(name = "WRITER_UUID")
	private String writer_uuid;


	public Timestamp getAction_timestamp() {
		return action_timestamp;
	}

	public void setAction_timestamp(Timestamp action_timestamp) {
		this.action_timestamp = action_timestamp;
	}

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

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getWriter_uuid() {
		return writer_uuid;
	}

	public void setWriter_uuid(String writer_uuid) {
		this.writer_uuid = writer_uuid;
	}
	
	public MessageIdentity getMessageId() {
		return messageId;
	}

	public void setMessageId(MessageIdentity messageId) {
		this.messageId = messageId;
	}

	@Override
	public String toString() {
		return "RelationEventJSON [messageId=" + messageId + ", action_timestamp=" + action_timestamp + ", event=" + event
				+ ", ser_manifest=" + ser_manifest + ", tags=" + tags + ", writer_uuid=" + writer_uuid + "]";
	}

}
