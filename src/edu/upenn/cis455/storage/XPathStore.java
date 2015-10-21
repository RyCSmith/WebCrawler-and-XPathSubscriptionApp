package edu.upenn.cis455.storage;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class XPathStore {
	PrimaryIndex<String, XPath> pIdx;
	
    public XPathStore(EntityStore store) throws DatabaseException {
        pIdx = store.getPrimaryIndex(String.class, XPath.class);
    }
}
