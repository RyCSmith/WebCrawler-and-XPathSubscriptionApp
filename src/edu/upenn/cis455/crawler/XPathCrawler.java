package edu.upenn.cis455.crawler;


public class XPathCrawler {
	public static void main(String[] args) {
		//assign command line args settings
		String startURL;
		String databasePath;
		int maxSizeMB;
		int maxNumFiles;
		if (args.length < 3) {
			System.out.println("Improper arguments.");
			System.exit(1);
		}
		startURL = args[0];
		databasePath = args[1];
		maxSizeMB = Integer.parseInt(args[2]);
		if (args.length == 4)
			maxNumFiles = Integer.parseInt(args[3]);
		
		
		
		
	}
}
