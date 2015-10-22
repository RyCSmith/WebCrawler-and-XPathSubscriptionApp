package edu.upenn.cis455.crawler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.regex.*;

import edu.upenn.cis455.info.DomainQueue;
import edu.upenn.cis455.info.RobotsTxtInfo;
import edu.upenn.cis455.info.URLHelper;
import edu.upenn.cis455.httpclient.HttpClient;
import edu.upenn.cis455.httpclient.HttpsClient;
public class CrawlerResources {

    public static ArrayList<String> parseForLinks(String documentText) {
        ArrayList<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))"); //"href=\"([^\"]*)\"" this works too
        Matcher matcher = pattern.matcher(documentText);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }
    
    public static void qualifyLinks(String url, ArrayList<String> links) {
    	String fullDomain = URLHelper.removeFilePath(url);
    	for (int i = 0; i < links.size(); i++) {
    		String link = links.get(i);
    		link = link.substring(link.indexOf("\"") + 1);
    		link = link.substring(0, link.lastIndexOf("\""));
    		if (!link.startsWith("http")) {
	    		if (link.startsWith("/")){
	    			links.set(i, fullDomain + link);
	    		}
	    		else {
	    			links.set(i, fullDomain + "/" + link);
	    		}
    		}
    	}
    }
    public static RobotsTxtInfo processRobotsTxt(String url) {
    	String robotsText = fetchRobotsTxtText(url);
    	String[] lines = robotsText.split("\n");
    	String bestUserAgent = findBestUserAgent(lines);
    	String[] relevantLines = separateRelevantLines(lines, bestUserAgent);
    	RobotsTxtInfo robotsTxt = new RobotsTxtInfo();
    	robotsTxt.setUserAgent(bestUserAgent);
    	for (String line : relevantLines) {
    		String[] pieces = null;
    		if (line.indexOf(":") > 0)
				pieces = line.split(":"); 
			if (pieces != null && pieces.length == 2){
	    		String type = pieces[0].trim();
	    		if (type.equalsIgnoreCase("Disallow"))
	    			robotsTxt.addDisallowedLink(pieces[1].trim());
	    		else if (type.equalsIgnoreCase("Allowed"))
	    			robotsTxt.addAllowedLink(pieces[1].trim());
	    		else if (type.equalsIgnoreCase("Crawl-delay"))
	    			robotsTxt.setCrawlDelay(pieces[1].trim());
			}
    	}
		return robotsTxt;
    }
    
    public static String findBestUserAgent(String[] lines) {
    	boolean allFound = false;
    	for (String line : lines) {
    		if (line.contains("User-agent")) {
    			if (line.contains("cis455crawler"))
    				return "cis455crawler";
    			else if (line.contains("*"))
    				allFound = true;
    		}
    	}
    	if (allFound)
    		return "*";
    	return null;
    }
    
    public static String[] separateRelevantLines(String[] lines, String bestUserAgent) {
    	int startIndex = 0;
    	int endIndex = 0;
    	if (bestUserAgent != null) {
    		for (int i = 0; i < lines.length; i++){
    			if (lines[i].contains("User-agent")){
	    			if (lines[i].contains(bestUserAgent)) {
	    				endIndex = 0;
	    				startIndex = i;
	    			}
	    			else
	    				endIndex = i;
    			}
    		}
    		if (endIndex == 0)
    			endIndex = lines.length;
    		return Arrays.copyOfRange(lines, startIndex, endIndex);
    	}
    	else { //we enter this case when we have established that all user agents are not referring to us (we want to disregard all instructions under them)
    		for (int i = 0; i < lines.length; i++) {
    			if (lines[i].contains("User-agent")){
    				startIndex = i;
    				break;
    			}
    		}
    		return Arrays.copyOfRange(lines, 0, startIndex);
    	}
    }
    
    private static String fetchRobotsTxtText(String url) {
    	try {
	    	URLHelper.Protocol protocol = URLHelper.getProtocol(url);
	    	url = URLHelper.removeFilePath(url);
	    	url = url + "/robots.txt";
	    	HttpClient client = null;
	    	switch(protocol) {
	    	case HTTP:
	    		client = new HttpClient(url, "GET");
	    		break;
	    	case HTTPS:
	    		client = new HttpsClient(url, "GET");
	    	}
	    	client.makeRequest();
	    	return client.getDocument();
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public static HttpClient attemptRequest(String url, String method) {
    	try {
    		URLHelper.Protocol protocol = URLHelper.getProtocol(url);
	    	HttpClient client = null;
	    	switch(protocol) {
	    	case HTTP:
	    		client = new HttpClient(url, method);
	    		break;
	    	case HTTPS:
	    		client = new HttpsClient(url, method);
	    	}
	    	client.makeRequest();
	    	return client;
    	}catch (Exception e) {
    		return null;
    	}
    }
    

    
}
