package test.edu.upenn.cis455;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import edu.upenn.cis455.httpclient.HttpClient;

public class HttpClientTest {
	HttpClient client;
	
	@Before
	public void setUp() throws Exception {
		final String URL = "http://www.w3schools.com/xml/plant_catalog.xml";
		client = new HttpClient(URL);
		client.makeRequest();
	}

	@Test
	public void testGetDocument() throws IOException {
		String retrievedDocument = client.getDocument();
		assertNotNull(retrievedDocument);
		assertTrue(retrievedDocument.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		System.out.println(client.getDocument());
	}
	
	@Test
	public void testGetHeaders() {
		assertEquals(client.getResponseHeader("Content-Length"), "7516");
		assertEquals(client.getResponseHeader("ResponseCode"), "200");
		assertEquals(client.getResponseHeader("ResponseMessage"), "OK");
	}

}
