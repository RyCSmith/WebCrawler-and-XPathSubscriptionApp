package edu.upenn.cis455.storage;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class UserStore {
	PrimaryIndex<String, User> pIdx;
	
    public UserStore(EntityStore store) throws DatabaseException {
        pIdx = store.getPrimaryIndex(String.class, User.class);
    }
}
