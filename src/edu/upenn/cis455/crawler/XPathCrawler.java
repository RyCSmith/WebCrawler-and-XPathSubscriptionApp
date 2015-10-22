package edu.upenn.cis455.crawler;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import edu.upenn.cis455.info.DomainQueue;
import edu.upenn.cis455.info.RobotsTxtInfo;
import edu.upenn.cis455.httpclient.HttpClient;
import edu.upenn.cis455.httpclient.HttpsClient;
import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.URLData;
import edu.upenn.cis455.info.URLHelper;


public class XPathCrawler {
	String startURL;
	int maxSizeBytes;
	int maxNumFiles;
	Queue<DomainQueue> masterQueue;
	HashMap<String, DomainQueue> currentQueueContents;
	DBWrapper database;
	HashSet<String> crawledThisSession;
	
	public static void main(String[] args) {
		new XPathCrawler(args);
	}
	
	public XPathCrawler(String[] args) {
		if (args.length < 3) {
			System.out.println("Improper arguments.");
			System.exit(1);
		}
		try {
			//initialize variables
			startURL = args[0];
			database = new DBWrapper(args[1]);
			database.openDB();
			maxSizeBytes = Integer.parseInt(args[2]) * 1048576; //convert MB to bytes
			if (args.length == 4)
				maxNumFiles = Integer.parseInt(args[3]);
			else
				maxNumFiles = Integer.MAX_VALUE;
			masterQueue = new LinkedList<DomainQueue>();
			crawledThisSession = new HashSet<String>();
			currentQueueContents = new HashMap<String, DomainQueue>();
			//process command line URL
			RobotsTxtInfo robotstxt = CrawlerResources.processRobotsTxt(startURL);
			String domain = URLHelper.extractDomain(startURL);
			DomainQueue initialDomainQueue = new DomainQueue(domain);
			currentQueueContents.put(domain, initialDomainQueue);
			initialDomainQueue.setRobotsInfo(robotstxt);
			initialDomainQueue.addToQueue(startURL);
			masterQueue.offer(initialDomainQueue);
			run();
			//database.testPrint();
			database.closeDB();
		} catch (Exception e) {
			System.out.println("An error occurred. Here is its stack trace:");
			e.printStackTrace();
		} finally {
			if (database != null)
				database.closeDB();
		}
	}

	public void run() {
		int docsReceived = 0;
		while (!masterQueue.isEmpty() && docsReceived < maxNumFiles){
			//get next element - check crawl delay and return to end of queue if crawled too recently
			DomainQueue domainQ = masterQueue.poll();
			int crawlDelay = domainQ.getRobotsInfo().getCrawlDelay();
			if ((domainQ.getLastCrawled() + crawlDelay) > System.currentTimeMillis()){
				masterQueue.offer(domainQ);
				continue;
			}
			
			String url = domainQ.getNext();
			System.out.println("Working on this URL: " + url);
			if (domainQ.hasNext()) //if it is valid remember to return the DomainQueue if it's holding more URLs 
				masterQueue.offer(domainQ);
			else
				currentQueueContents.remove(domainQ.getDomain()); //otherwise removed from map of current queue domains
			
			HttpClient client = CrawlerResources.attemptRequest(url, "HEAD");
			crawledThisSession.add(url); //records url so that it will not be crawled again in this execution
			if ((client.getContentLength() > maxSizeBytes) || 
					(client.getContentType() == HttpClient.Type.UNKNOWN)) {
				continue; //this effectively throws away urls that do not meet our constraints
			}
			
			URLData data = database.getURLData(url);
			if (data != null) { //case = URL exists in DB - has been crawled before
				if (data.getLastAccessed() > client.getLastModified()) { //case = content has not been modified since last crawled
					updateQueues(url, data.getContent());
					database.updateLastAccessed(url);
					docsReceived++;
					continue;
				}
				else { //case = content has been modified since last crawl 
					HttpClient getClient = CrawlerResources.attemptRequest(url, "GET");
					if (getClient != null) {
						database.updateURLData(url, getClient);
						if (getClient.getContentType() == HttpClient.Type.XML)
							database.updateXPaths(url, getClient.getDocument(), getClient.getContentType());
						updateQueues(url, getClient.getDocument());
						docsReceived++;
					}
					continue;
				}
			}
			else { //case = URL does not exist in DB
				HttpClient getClient = CrawlerResources.attemptRequest(url, "GET");
				if (getClient != null) {
					database.addNewURLData(url, getClient);
					if (getClient.getContentType() == HttpClient.Type.XML)
						database.updateXPaths(url, getClient.getDocument(), getClient.getContentType());
					updateQueues(url, getClient.getDocument());
					docsReceived++;
				}
			}
			
		}
	}
	
    public void updateQueues(String url, String document) {
    	ArrayList<String> links = CrawlerResources.parseForLinks(document);
		CrawlerResources.qualifyLinks(url, links);
    	for (String link : links) { 
    		if (crawledThisSession.contains(link)) //case = link has already been crawled this session, do not add to queues
    			continue;
    		String domain = URLHelper.extractDomain(link);
    		DomainQueue current = currentQueueContents.get(domain);
    		if (current != null) { //case = a queue for this domain already exists - add link to queue
    			current.addToQueue(link);
    		}
    		else { //case = a queue for this domain does not exist - create new DomainQueue and add to masters
    			RobotsTxtInfo robotsTxt = CrawlerResources.processRobotsTxt(link);
    			DomainQueue newDomainQueue = new DomainQueue(domain);
    			currentQueueContents.put(domain, newDomainQueue);
    			newDomainQueue.setRobotsInfo(robotsTxt);
    			newDomainQueue.addToQueue(link);
    			masterQueue.offer(newDomainQueue);
    		}
    	}
    }
}
