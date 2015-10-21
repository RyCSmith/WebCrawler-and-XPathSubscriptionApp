package edu.upenn.cis455.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;

import edu.upenn.cis455.crawler.info.URLInfo;
import edu.upenn.cis455.httpclient.HttpClient;
import edu.upenn.cis455.xpathengine.Parser;

public class ServletSupport {

	public static String[] getXPathsFromPost(HttpServletRequest request) {
		ArrayList<String> xpathList = new ArrayList<String>();
		Map<String, String[]> param = request.getParameterMap();
		for (String key : param.keySet()) {
			if (key.startsWith("xpath")){
				String[] namedParams = param.get(key);
				if (namedParams[0] != null)
					xpathList.add(namedParams[0]);
			}
		}
		String[] xpathArray = new String[xpathList.size()];
		for (int i = 0; i < xpathList.size(); i++) {
			xpathArray[i] = xpathList.get(i);
		}
		return xpathArray;
	}
	
	public static boolean checkIsLocal(String url) {
		URLInfo info = new URLInfo(url);
		return (info.getHostName() == null);
	}
	
	public static String cleanHTML(String documentString) throws UnsupportedEncodingException {
			Tidy tidy = new Tidy();
			tidy.setQuiet(true);
			tidy.setShowWarnings(false);
			tidy.setMakeClean(true);
			tidy.setXHTML(true);
			tidy.setSmartIndent(true);
			Document tidyDoc = tidy.parseDOM( new ByteArrayInputStream(documentString.getBytes()), null);
			ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
			tidy.pprint(tidyDoc, outputBytes);
			StringBuilder builder = new StringBuilder();
			builder.append(new String(outputBytes.toByteArray(), "UTF-8"));
			String cleanHTMLString = builder.toString();
			return cleanHTMLString;
	}
	public static String readFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder text = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
            text.append(line);
        }
        reader.close();
        return text.toString();
	}
	@SuppressWarnings("finally")
	public static Document buildDocument(String documentString, boolean isLocal) {
		try {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(false);
	        factory.setValidating(false);
	        factory.setFeature("http://xml.org/sax/features/namespaces", false);
	        factory.setFeature("http://xml.org/sax/features/validation", false);
	        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
	        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); 
	        DocumentBuilder parser = factory.newDocumentBuilder();
			Document domDoc;
			if (isLocal) {
				if (documentString.endsWith(".html")){
					File file = new File(documentString);
					String fileText = readFile(file);
					fileText = cleanHTML(fileText);
					InputSource inputSource = new InputSource(new StringReader(fileText));
					domDoc = parser.parse(inputSource); 
				}
				else {
					File inputSource = new File(documentString);
					domDoc = parser.parse(inputSource); 
				}
			}
			else {
				
				if (!documentString.startsWith("<?xml version")){
					documentString = cleanHTML(documentString);
				}
				InputSource inputSource = new InputSource(new StringReader(documentString));
				domDoc = parser.parse(inputSource); 
				
			}   
	        domDoc.getDocumentElement().normalize();
	        return domDoc;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Document buildDocFromString(String documentString, HttpClient.Type type) {
		try {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(false);
	        factory.setValidating(false);
	        factory.setFeature("http://xml.org/sax/features/namespaces", false);
	        factory.setFeature("http://xml.org/sax/features/validation", false);
	        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
	        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); 
	        DocumentBuilder parser = factory.newDocumentBuilder();
			Document domDoc;
			if (type == HttpClient.Type.HTML){
				documentString = cleanHTML(documentString);
			}
			InputSource inputSource = new InputSource(new StringReader(documentString));
			domDoc = parser.parse(inputSource); 
	        domDoc.getDocumentElement().normalize();
	        return domDoc;
	        
		} catch (Exception e) {
			return null;
		}
	}
}
