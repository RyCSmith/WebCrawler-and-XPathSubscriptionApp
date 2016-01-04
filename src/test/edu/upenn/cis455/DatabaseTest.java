package test.edu.upenn.cis455;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.upenn.cis455.crawler.XPathCrawler;
import edu.upenn.cis455.crawler.XPathCrawlerFactory;
import edu.upenn.cis455.storage.DBWrapper;

public class DatabaseTest {

	//Please note: this path will need a backslash instead if testing on Windows
	String testDBPath = System.getProperty("user.dir") + "/StaticTestDB";
	DBWrapper database;
	
	@Before
	public void setUp() throws Exception {
		database = new DBWrapper(testDBPath);
		database.openDB();
	}
	
	@After
	public void tearDown() {
		database.closeDB();
	}

	@Test
	public void totalDocsCountTest() {
		assertEquals(36, database.totalDocsContained());
	}
	
	@Test
	public void checkUserExistsTest() {
		assertTrue(database.checkUserExists("testUser"));
		assertFalse(database.checkUserExists("badUser"));
	}
	
	@Test
	public void checkPasswordTest() {
		assertTrue(database.checkPassword("testUser", "testPass"));
		assertFalse(database.checkPassword("testUser", "badPass"));
	}
}
