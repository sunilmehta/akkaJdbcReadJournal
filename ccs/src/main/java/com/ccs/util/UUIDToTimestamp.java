package com.ccs.util;

import java.sql.Timestamp;
import java.util.UUID;

public class UUIDToTimestamp {
	  // This method comes from Hector's TimeUUIDUtils class:
	  // https://github.com/rantav/hector/blob/master/core/src/main/java/me/prettyprint/cassandra/utils/TimeUUIDUtils.java
	  static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;
	  private static long getTimeFromUUID(UUID uuid) {
	    return (uuid.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000;
	  }

	  public static Timestamp getTimestamp(String uuidString) {
	    UUID uuid = UUID.fromString(uuidString);
	    long time = getTimeFromUUID(uuid);
	    return new Timestamp(time);
	  }
	}