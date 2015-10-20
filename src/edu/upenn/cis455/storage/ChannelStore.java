package edu.upenn.cis455.storage;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class ChannelStore {
	PrimaryIndex<String, Channel> pIdx;
	
    public ChannelStore(EntityStore store) throws DatabaseException {
        pIdx = store.getPrimaryIndex(String.class, Channel.class);
    }
}
