package edu.upenn.cis455.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import java.text.ParseException;

import edu.upenn.cis455.crawler.info.URLInfo;

public class HttpClient {
	URLInfo urlInfo;
	HashMap<String, String> requestHeaders;
	HashMap<String, String> responseData;
	public enum Type {XML, HTML, RSS, UNKNOWN}
	String method;
	
	public HttpClient(String url, String method) {
		requestHeaders = new HashMap<String, String>();
		responseData = new HashMap<String, String>();
		this.method = method;
		urlInfo = new URLInfo(url);
	}
	
	public void makeRequest() throws IOException {
		InetAddress ip = InetAddress.getByName(urlInfo.getHostName());
		Socket socket = new Socket(ip, urlInfo.getPortNo());
		String request = getHeaders();
		byte[] response = request.getBytes("UTF-8");
		OutputStream outStream = socket.getOutputStream();
		outStream.write(response,0,response.length);
		outStream.flush();
		readResponse(socket.getInputStream());
		socket.close();
	}
	
	protected void readResponse(InputStream inStream) throws IOException {
		StringBuilder response = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
		String line;
		boolean pastBreak = false;
		while ((line = in.readLine()) != null) {
			if (line.startsWith("HTTP/1.")){
				String[] pieces = line.split(" ");
				if (pieces.length < 3)
					throw new IllegalStateException("HTTP header line had improper format.");
				responseData.put("Protocol", pieces[0]);
				responseData.put("ResponseCode", pieces[1]);
				StringBuilder message = new StringBuilder();
				for (int i = 2; i < pieces.length; i++) {
					message.append(pieces[i]);
				}
				responseData.put("ResponseMessage", message.toString());
			}
			else if (line.equals(""))
					pastBreak = true;
			else if (!pastBreak) {
				responseData.put(line.substring(0, line.indexOf(":")).trim(), line.substring(line.indexOf(":") + 1).trim());
			}
			else {
				response.append(line + "\n");
			}
		}
		responseData.put("Body", response.toString());
		in.close();
	}
	
	protected String getHeaders() {
		StringBuilder headerString = new StringBuilder();
		headerString.append(method + " " + urlInfo.getFilePath() + " HTTP/1.0\r\n");
		headerString.append("User-Agent: cis455crawler\r\n");
		if (!checkHostHeader()) 
			headerString.append("Host: " + urlInfo.getHostName() + "\r\n");
		for (String key : requestHeaders.keySet()) {
			headerString.append(key + ": " + requestHeaders.get(key) + "\r\n");
		}
		headerString.append("\r\n");
		return headerString.toString();
	}
	
	private boolean checkHostHeader() {
		for (String key : requestHeaders.keySet()) {
			if (key.equalsIgnoreCase("host"))
				return true;
		}
		return false;
	}
	
	public Type getContentType() {
		String typeString = responseData.get("Content-Type");
		if (typeString == null) {
			String document = getDocument();
			if (document.startsWith("<?xml"))
				return Type.XML;
			else if (document.startsWith("<!DOCTYPE html>"))
				return Type.HTML;
			else
				return Type.UNKNOWN;
		}
		if (typeString.equalsIgnoreCase("text/html"))
			return Type.HTML;
		else if (typeString.equalsIgnoreCase("text/xml"))
			return Type.XML;
		else if (typeString.equalsIgnoreCase("application/xml"))
			return Type.XML;
		else if (typeString.equalsIgnoreCase("application/rss"))
			return Type.RSS;
		else if (typeString.equalsIgnoreCase("application/rss+xml"))
			return Type.XML;
		else if (typeString.matches(".*(text/html|TEXT/HTML).*"))
			return Type.HTML;
		else if (typeString.matches(".*(text/xml|TEXT/XML).*"))
			return Type.XML;
		else if (typeString.matches(".*(application/xml|APPLICATION/XML).*"))
			return Type.XML;
		else if (typeString.matches(".*(application/rss|APPLICATION/RSS).*"))
			return Type.RSS;
		else if (typeString.matches(".*\\+(xml|XML).*"))
			return Type.XML;
		else
			return Type.UNKNOWN;
	}
	
	public long getLastModified() {
		String dateString = responseData.get("Last-Modified");
		if (dateString == null) 
			return -1;
		Date requestDate;
		try {
			SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
			requestDate = format.parse(dateString);
		} catch (ParseException e0) {
			try {
				SimpleDateFormat format = new SimpleDateFormat("EEE, dd-MMM-yy HH:mm:ss zzz");
				requestDate = format.parse(dateString);
			}catch (ParseException e1) {
				try {
					SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
					requestDate = format.parse(dateString);
				}catch (ParseException e2) {
					requestDate = null;
				}
			}
		}
		if (requestDate == null)
			return -1;
		Calendar requestTime = Calendar.getInstance();
		requestTime.setTime(requestDate);
		return requestTime.getTimeInMillis();
	}
	
	public int getContentLength() {
		String lengthString = responseData.get("Content-Length");
		if (lengthString == null)
			return -1;
		int length;
		try {
			length = Integer.parseInt(lengthString);
		} catch (Exception e) {
			return -1;
		}
		return length;
	}
	
	public void addHeader(String name, String value) {
		requestHeaders.put(name, value);
	}
	
	public String getResponseHeader(String name) {
		return responseData.get(name);
	}
	
	public String getDocument() {
		return responseData.get("Body");
	}

}
