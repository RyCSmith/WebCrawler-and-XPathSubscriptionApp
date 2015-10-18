package edu.upenn.cis455.httpclient;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TEst {

	public static void main(String[] args) {
		HttpClient client = new HttpClient("http://www.cis.upenn.edu");
		try {
			client.makeRequest();
			String document = client.getDocument();
			System.out.println(document);
			InputSource inputSource = new InputSource( new StringReader( document ) );
	        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder parser = docFactory.newDocumentBuilder();
	        Document dom = parser.parse(inputSource); 
	    }
		catch (IOException e) {
			e.printStackTrace();
		}
	    catch (SAXException e) {
	    }
	    catch (FactoryConfigurationError e) { 
	    }
	    catch (ParserConfigurationException e) { 
	    }
	   
	  }
}
