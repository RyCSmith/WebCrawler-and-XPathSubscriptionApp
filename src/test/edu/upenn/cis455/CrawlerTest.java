package test.edu.upenn.cis455;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.upenn.cis455.crawler.XPathCrawler;
import edu.upenn.cis455.crawler.XPathCrawlerFactory;
import edu.upenn.cis455.storage.DBWrapper;

/*
 * PLEASE NOTE: Due to crawl delay this test will take ~ 2 mins to complete.
 */

public class CrawlerTest {
	//Please note: this path will need a backslash instead if testing on Windows
	String testDBPath = System.getProperty("user.dir") + "/LocalDB";
	XPathCrawler crawler;
	DBWrapper database;
	
	@Before
	public void setUp() throws Exception {
		crawler = XPathCrawlerFactory.getCrawler();
		database = new DBWrapper(testDBPath);
		database.openDB();
	}
	
	@After
	public void tearDown() {
		database.closeDB();
		//this clears the DB so it will have to be created each time the test is run
		File directory = new File(testDBPath);
		for(File file: directory.listFiles()) 
			file.delete();
	}

	@Test
	public void test() {
		String sandboxStart = "https://dbappserv.cis.upenn.edu/crawltest/";
		assertEquals(0, database.totalDocsContained());
		String[] args = { sandboxStart, testDBPath, "5" };
		crawler.initializeAndRun(args);
		assertEquals(36, database.totalDocsContained());
	}

}
