package edu.upenn.cis455.storage;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

import edu.upenn.cis455.httpclient.HttpClient;
import edu.upenn.cis455.servlet.ServletSupport;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;


public class DBWrapper {
	
	private static File envHome;
    private Environment envmnt;
    private EntityStore store;
    private UserStore userStore;
    private UrlDataStore dataStore;
    private ChannelStore channelStore;
    private XPathStore xpathStore;
	
    public DBWrapper(String location) {
    	envHome = new File(location);
    }
    
	public void openDB() {
		EnvironmentConfig envConfig = new EnvironmentConfig();
        StoreConfig storeConfig = new StoreConfig();
        envConfig.setAllowCreate(true);
        storeConfig.setAllowCreate(true);
        envmnt = new Environment(envHome, envConfig);
        store = new EntityStore(envmnt, "EntityStore", storeConfig);
        //initializing the store objects
	    userStore = new UserStore(store);
	    dataStore = new UrlDataStore(store);
	    channelStore = new ChannelStore(store);
	    xpathStore = new XPathStore(store);
	}
	
	public void closeDB() throws DatabaseException {
        store.close();
        envmnt.close();
    }
	
	public URLData getURLData(String url) {
		URLData found = dataStore.pIdx.get(url);
		return found;
	}
	
	public void updateURLData(String url, HttpClient client) {
		URLData currentData = dataStore.pIdx.get(url);
		HttpClient.Type docType = client.getContentType();
		switch (docType) {
		case XML:
			currentData.setType(URLData.Type.XML);
			break;
		case HTML:
			currentData.setType(URLData.Type.HTML);
			break;
		case RSS:
			currentData.setType(URLData.Type.RSS);
		}
		currentData.setContentSize(client.getContentLength());
		currentData.setContent(client.getDocument());
		currentData.setLastAccessed(System.currentTimeMillis());
		//dataStore.pIdx.put(currentData);
	}
	
	public void updateLastAccessed(String url) {
		URLData currentData = dataStore.pIdx.get(url);
		currentData.setLastAccessed(System.currentTimeMillis());
		//dataStore.pIdx.put(currentData);
	}
	
	public void addNewURLData(String url, HttpClient client) {
		URLData newURL = new URLData();
	    newURL.setUrl(url);
	    newURL.setContent(client.getDocument());
	    newURL.setContentSize(client.getContentLength());
	    newURL.setLastAccessed(System.currentTimeMillis());
	    HttpClient.Type docType = client.getContentType();
		switch (docType) {
		case XML:
			newURL.setType(URLData.Type.XML);
			break;
		case HTML:
			newURL.setType(URLData.Type.HTML);
			break;
		case RSS:
			newURL.setType(URLData.Type.RSS);
		}
	    dataStore.pIdx.put(newURL);
	}
	
	public void updateXPaths(String url, String document, HttpClient.Type type) {
		PrimaryIndex<String,XPath> xpathIterator = store.getPrimaryIndex(String.class, XPath.class);
		EntityCursor<XPath> xp_cursor = xpathIterator.entities();
		for (XPath path : xp_cursor) {
			if (path.getArticleUrls().contains(url)) //case = this XPath object has already been matched to this URL
				continue;
			XPathEngineImpl engine = new XPathEngineImpl();
			String[] paths = { path.getXpathString() };
			engine.setXPaths(paths);
			Document domDoc = ServletSupport.buildDocFromString(document, type);
			boolean[] results = engine.evaluate(domDoc);
			if (results[0]) {
				path.getArticleUrls().add(url);
				//xpathStore.pIdx.put(path);
			}
		}
	}
	
	public void testPrint() {
		PrimaryIndex<String, URLData> data = store.getPrimaryIndex(String.class, URLData.class);
		EntityCursor<URLData> data_cursor = data.entities();
		for (URLData current : data_cursor) {
			System.out.println(current.getUrl() + current.getLastAccessed());
		}
	}
	
}
