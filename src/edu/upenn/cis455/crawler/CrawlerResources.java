package edu.upenn.cis455.crawler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import edu.upenn.cis455.crawler.info.RobotsTxtInfo;
import edu.upenn.cis455.crawler.info.URLHelper;
import edu.upenn.cis455.httpclient.HttpClient;
import edu.upenn.cis455.httpclient.HttpsClient;
public class CrawlerResources {

    public static List<String> extractUrls(String input) {
        List<String> result = new ArrayList<String>();
        HttpClient client = new HttpClient("http://www.google.com", "GET");
        try {
			client.makeRequest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Pattern pattern = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
        //"href=\"([^\"]*)\"" this works too
        Matcher matcher = pattern.matcher(client.getDocument());
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }
    
    public static RobotsTxtInfo processRobotsTxt(String url) {
    	String robotsText = fetchRobotsTxtText(url);
    	String[] lines = robotsText.split("\n");
    	return null;
    }
    
    private static String fetchRobotsTxtText(String url) {
    	try {
	    	URLHelper.Protocol protocol = URLHelper.getProtocol(url);
	    	url = URLHelper.removeFilePath(url);
	    	url = url + "/robots.txt";
	    	StringBuilder builder = new StringBuilder();
	    	HttpClient client = null;
	    	switch(protocol) {
	    	case HTTP:
	    		client = new HttpClient(url, "GET");
	    	case HTTPS:
	    		client = new HttpsClient(url, "GET");
	    	}
	    	client.makeRequest();
	    	return client.getDocument();
    	} catch (Exception e) {
    		return null;
    	}
    }
    

    
}
