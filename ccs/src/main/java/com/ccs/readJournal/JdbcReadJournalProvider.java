package com.ccs.readJournal;

import com.typesafe.config.Config;

import akka.actor.ExtendedActorSystem;
import akka.persistence.query.ReadJournalProvider;
import akka.persistence.query.javadsl.ReadJournal;

public class JdbcReadJournalProvider implements ReadJournalProvider
{

	private final JdbcReadJournal jdbcReadJournal;

	public JdbcReadJournalProvider(ExtendedActorSystem system, Config config, String configPath) {
		jdbcReadJournal = new JdbcReadJournal(system, config);
	}

	@Override
	public ReadJournal javadslReadJournal() {
		return jdbcReadJournal;
	}

	@Override
	public akka.persistence.query.scaladsl.ReadJournal scaladslReadJournal() {
		// TODO Auto-generated method stub
		return null;
	}
}
