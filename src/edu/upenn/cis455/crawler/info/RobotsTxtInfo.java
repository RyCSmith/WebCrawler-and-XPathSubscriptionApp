package edu.upenn.cis455.crawler.info;

import java.util.HashSet;

public class RobotsTxtInfo {
	
	private HashSet<String> disallowedLinks;
	private HashSet<String> allowedLinks;
	
	private int crawlDelay;
	private String userAgent; //indicates user agent that this is following
	
	public RobotsTxtInfo(){
		disallowedLinks = new HashSet<String>();
		allowedLinks = new HashSet<String>();
		crawlDelay = 0;
		userAgent = null;
	}
	
	public void addDisallowedLink(String link){
		disallowedLinks.add(link);
	}
	
	public void addAllowedLink(String link){
		allowedLinks.add(link);
	}
	
	public void setCrawlDelay(String delay){
		try {
			crawlDelay = Integer.parseInt(delay) * 1000; //convert to millis
		} catch (Exception e) {}
	}
	
	public void setUserAgent(String agent){
		userAgent = agent;
	}
	
	public HashSet<String> getDisallowedLinks(){
		return disallowedLinks;
	}
	
	public HashSet<String> getAllowedLinks(){
		return allowedLinks;
	}
	
	public int getCrawlDelay(){
		return crawlDelay;
	}
	
	public String getUserAgent(){
		return userAgent;
	}
}
