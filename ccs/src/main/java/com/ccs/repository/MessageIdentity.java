package com.ccs.repository;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MessageIdentity implements Serializable {

	private static final long serialVersionUID = 2977904614659915025L;

	@Column(name = "persistence_id")
	protected String persistenceId;

	@Column(name = "sequence_nr")
	protected long sequenceNr;

	public MessageIdentity( String persistanceId, long sequenceNr) {
		this.persistenceId = persistanceId;
		this.sequenceNr = sequenceNr;
	}

	

	
	public String getPersistenceId() {
		return persistenceId;
	}




	public void setPersistenceId(String persistenceId) {
		this.persistenceId = persistenceId;
	}




	public long getSequenceNr() {
		return sequenceNr;
	}




	public void setSequenceNr(long sequenceNr) {
		this.sequenceNr = sequenceNr;
	}




	public MessageIdentity() {
		super();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof  MessageIdentity))
			return false;
		else {
			MessageIdentity messId = (MessageIdentity) obj;
			if(messId.getPersistenceId().equals(this.persistenceId) && messId.getSequenceNr() == (this.sequenceNr)) {
				return true ;
			} else {
				return false;
			}
			
		}
	}
	
	 @Override
	    public int hashCode() {
	      final int prime = 31;
	      int result = 1 ;
	      result = prime * result + persistenceId.hashCode();
	      return result;
	    }

}
