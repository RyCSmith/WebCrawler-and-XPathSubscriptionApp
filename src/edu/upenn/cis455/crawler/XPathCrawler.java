package edu.upenn.cis455.crawler;


public class XPathCrawler {
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Improper arguments.");
			System.exit(1);
		}
		CrawlerResources.extractUrls(null);
		
	}
}
