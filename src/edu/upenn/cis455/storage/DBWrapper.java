package edu.upenn.cis455.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import edu.upenn.cis455.xpathengine.XPathEngineImpl;
import edu.upenn.cis455.xpathengine.DocumentServices;
import edu.upenn.cis455.httpclient.HttpClient.Type;


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
		dataStore.pIdx.put(currentData);
	}
	
	public void updateLastAccessed(String url) {
		URLData currentData = dataStore.pIdx.get(url);
		currentData.setLastAccessed(System.currentTimeMillis());
		dataStore.pIdx.put(currentData);
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
			Document domDoc = DocumentServices.buildDocFromString(document, type);
			boolean[] results = engine.evaluate(domDoc);
			if (results[0]) {
				path.getArticleUrls().add(url);
				xpathStore.pIdx.put(path);
			}
		}
		xp_cursor.close();
	}
	
	public boolean checkUserExists(String username) {
		User u = userStore.pIdx.get(username);
		return u != null;
	}
	
	public boolean checkPassword(String username, String password) {
		User u = userStore.pIdx.get(username);
		if (u == null)
			return false;
		if (u.getPassword().equals(password))
			return true;
		return false;
	}
	
	public void addUser(String username, String password) {
		User newUser = new User();
		newUser.setUsername(username);
		newUser.setPassword(password);
		userStore.pIdx.put(newUser);
	}
	
	public void addUser(String username, String password, String firstName, String lastName) {
		User newUser = new User();
		newUser.setUsername(username);
		newUser.setPassword(password);
		newUser.setFirstName(firstName);
		newUser.setLastName(lastName);
		userStore.pIdx.put(newUser);
	}
	
	public void testPrint() {
		PrimaryIndex<String, URLData> data = store.getPrimaryIndex(String.class, URLData.class);
		EntityCursor<URLData> data_cursor = data.entities();
		for (URLData current : data_cursor) {
			System.out.println(current.getUrl() + current.getLastAccessed());
		}
		data_cursor.close();
	}
	
	public Set<String> getUserChannels(String username) {
		User u = userStore.pIdx.get(username);
		if (u == null)
			return new HashSet<String>();
		int counter = 0;
		Set<String> channels = u.getChannels();		
		return u.getChannels();
	}
	
	public void addChannel(String username, String channelName, String[] xpaths) {	
		for (String path : xpaths) {
			XPath x = xpathStore.pIdx.get(path);
			if (x != null)
				continue;
			x = new XPath();
			x.setXpathString(path);
			matchNewXPath(x);
			xpathStore.pIdx.put(x);
		}
		Channel c = new Channel();
		c.setChannelPath(channelName);
		Set<String> pathSet = new HashSet<String>(Arrays.asList(xpaths)); 
		c.setXpaths(pathSet);
		channelStore.pIdx.put(c);
		User u = userStore.pIdx.get(username);
		Set<String> channels = u.getChannels();
		channels.add(c.getChannelPath());
		u.setChannels(channels);
		userStore.pIdx.put(u);
		
	}
	
	public void matchNewXPath(XPath xpath) {
		PrimaryIndex<String,URLData> urlIterator = store.getPrimaryIndex(String.class, URLData.class);
		EntityCursor<URLData> url_cursor = urlIterator.entities();
		for (URLData currentURL : url_cursor) {
			try {
				XPathEngineImpl engine = new XPathEngineImpl();
				String[] paths = { xpath.getXpathString() };
				engine.setXPaths(paths);
				Document domDoc;
				if (currentURL.getType() == URLData.Type.HTML)
					domDoc = DocumentServices.buildDocFromString(currentURL.getContent(), HttpClient.Type.HTML);
				else
					domDoc = DocumentServices.buildDocFromString(currentURL.getContent(), HttpClient.Type.XML);
				boolean[] results = engine.evaluate(domDoc);
				if (results[0]) {
					xpath.getArticleUrls().add(currentURL.getUrl());
				}
			} catch (Exception e) {}
		}
		url_cursor.close();
	}
	
	public void deleteChannel(String username, String channelName) {
		channelStore.pIdx.delete(channelName);
		User u = userStore.pIdx.get(username);
		Set<String> channels = u.getChannels();
		channels.remove(channelName);
		u.setChannels(channels);
		userStore.pIdx.put(u);
	}
	
	public HashMap<String, Set<URLData>> getChannelArticles(String channelName) {
		HashMap<String, Set<URLData>> articles = new HashMap<String, Set<URLData>>();
		Channel c = channelStore.pIdx.get(channelName);
		Set<String> paths = c.getXpaths();
		for (String path : paths) {
			XPath x = xpathStore.pIdx.get(path);
			Set<URLData> dataSet = new HashSet<URLData>();
			Set<String> urls = x.getArticleUrls();
			for (String url : urls) {
				URLData data = dataStore.pIdx.get(url);
				dataSet.add(data);
			}
			articles.put(x.getXpathString(), dataSet);
		}
		return articles;
	}
	
	public HashMap<String, Set<URLData>> getAllChannelsContent() {
		HashMap<String, Set<URLData>> channelsData = new HashMap<String, Set<URLData>>();
		PrimaryIndex<String, Channel> chan = store.getPrimaryIndex(String.class, Channel.class);
		EntityCursor<Channel> chan_cursor = chan.entities();
		for (Channel channel : chan_cursor) {
			Set<URLData> dataSet = new HashSet<URLData>();
			Set<String> paths = channel.getXpaths();
			for (String path : paths) {
				XPath x = xpathStore.pIdx.get(path);
				Set<String> urls = x.getArticleUrls();
				for (String url : urls) {
					URLData data = dataStore.pIdx.get(url);
					dataSet.add(data);
				}
			}
			channelsData.put(channel.getChannelPath(), dataSet);
		}
		chan_cursor.close();
		return channelsData;
	}
	
	public void deleteAllDocs() {
		PrimaryIndex<String, URLData> data = store.getPrimaryIndex(String.class, URLData.class);
		EntityCursor<URLData> data_cursor = data.entities();
		for (URLData d : data_cursor) {
			dataStore.pIdx.delete(d.getUrl());
		}
		data_cursor.close();
	}
	
	public int totalDocsContained() {
		int count = 0;
		PrimaryIndex<String, URLData> data = store.getPrimaryIndex(String.class, URLData.class);
		EntityCursor<URLData> data_cursor = data.entities();
		for (URLData d : data_cursor) {
			count++;
		}
		data_cursor.close();
		return count;
	}
}
