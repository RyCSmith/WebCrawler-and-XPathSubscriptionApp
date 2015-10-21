package edu.upenn.cis455.crawler.info;

import java.util.LinkedList;
import java.util.Queue;

public class DomainQueue {

	private String domain;
	private long lastCrawled;
	private Queue<String> queue;
	private RobotsTxtInfo robotsInfo;
	
	public DomainQueue(String domain) {
		this.domain = domain;
		queue = new LinkedList<String>();
		robotsInfo = new RobotsTxtInfo();
		lastCrawled = -1;
	}
	
	public String getNext() {
		return queue.poll();
	}
	
	public void addToQueue(String newURL) {
		queue.offer(newURL);
	}
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public RobotsTxtInfo getRobotsInfo() {
		return robotsInfo;
	}

	public void setRobotsInfo(RobotsTxtInfo robotsInfo) {
		this.robotsInfo = robotsInfo;
	}
	
	public boolean hasNext() {
		if (queue.isEmpty())
			return false;
		return true;
	}

	public long getLastCrawled() {
		return lastCrawled;
	}

	public void setLastCrawled(long lastCrawled) {
		this.lastCrawled = lastCrawled;
	}
	
	
	
}
