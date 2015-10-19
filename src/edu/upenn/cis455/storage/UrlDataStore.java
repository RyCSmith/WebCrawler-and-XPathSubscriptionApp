package edu.upenn.cis455.storage;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

public class UrlDataStore {
	PrimaryIndex<String, URLData> pIdx;
	
    public UrlDataStore(EntityStore store) throws DatabaseException {
        pIdx = store.getPrimaryIndex(String.class, URLData.class);
    }
}