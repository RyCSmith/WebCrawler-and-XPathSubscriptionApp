package edu.upenn.cis455.httpclient;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TEst {

	public static void main(String[] args) throws MalformedURLException {
//		HttpsClient client = new HttpsClient("https://www.yahoo.com");
		HttpClient client = new HttpClient("http://www.w3schools.com/xml/simple.xml");
		try {
			client.makeRequest();
			int a= 1;
			for (String key : client.responseData.keySet()) {
				System.out.println(key + ": " + client.responseData.get(key));
			}
			
			System.out.println("ContentLength"+ ": " +client.getContentLength());
			System.out.println("ContentType"+ ": " +client.getContentType());
			System.out.println("Last Modified"+ ": " +client.getLastModified());
//			String document = client.getDocument();
//			System.out.println(document);
//			InputSource inputSource = new InputSource( new StringReader( document ) );
//	        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//	        DocumentBuilder parser = docFactory.newDocumentBuilder();
//	        Document dom = parser.parse(inputSource); 
	    }
		catch (IOException e) {
			e.printStackTrace();
		}
//	    catch (SAXException e) {
//	    }
//	    catch (FactoryConfigurationError e) { 
//	    }
//	    catch (ParserConfigurationException e) { 
//	    }
	   
	  }
}
