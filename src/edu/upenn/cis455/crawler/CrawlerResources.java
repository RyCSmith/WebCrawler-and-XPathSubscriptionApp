package edu.upenn.cis455.crawler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import edu.upenn.cis455.httpclient.HttpClient;
public class CrawlerResources {

    public static List<String> extractUrls(String input) {
        List<String> result = new ArrayList<String>();
        HttpClient client = new HttpClient("http://www.google.com");
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
}
