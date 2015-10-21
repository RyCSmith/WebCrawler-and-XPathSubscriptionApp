package edu.upenn.cis455.crawler;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.Queue;

import edu.upenn.cis455.crawler.info.DomainQueue;
import edu.upenn.cis455.crawler.info.RobotsTxtInfo;
import edu.upenn.cis455.httpclient.HttpsClient;


public class XPathCrawler {
	String startURL;
	String databasePath;
	int maxSizeBytes;
	int maxNumFiles;
	Queue<DomainQueue> masterQueue;
	
	public static void main(String[] args) {
		new XPathCrawler(args).run();
	}
	
	public XPathCrawler(String[] args) {
		if (args.length < 3) {
			System.out.println("Improper arguments.");
			System.exit(1);
		}
		startURL = args[0];
		databasePath = args[1];
		maxSizeBytes = Integer.parseInt(args[2]) * 1048576; //convert MB to bytes
		if (args.length == 4)
			maxNumFiles = Integer.parseInt(args[3]);
		masterQueue = new LinkedList<DomainQueue>();
		RobotsTxtInfo robotstxt = CrawlerResources.processRobotsTxt(startURL);

	}

	public void run() {
		
	}
}
