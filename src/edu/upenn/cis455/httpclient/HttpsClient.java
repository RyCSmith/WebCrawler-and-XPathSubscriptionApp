package edu.upenn.cis455.httpclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import edu.upenn.cis455.crawler.info.URLInfo;

public class HttpsClient {
	URL url;
	HashMap<String, String> requestHeaders;
	HashMap<String, String> responseData;
	
	public HttpsClient(String url) throws MalformedURLException {
		requestHeaders = new HashMap<String, String>();
		responseData = new HashMap<String, String>();
		this.url = new URL(url);
	}
	
	public void makeRequest() throws IOException {
	    HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
	    String request = getHeaders();
		byte[] response = request.getBytes("UTF-8");
	    connection.setUseCaches(false);
	    connection.setDoOutput(true);

	    DataOutputStream outStream = new DataOutputStream (connection.getOutputStream());
	    outStream.write(response,0,response.length);
		outStream.flush();
		connection.getResponseCode();
		readResponse(connection);
	    outStream.close();

	}
	
	private void readResponse(HttpsURLConnection socket) throws IOException {
		StringBuilder response = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String line;
		boolean pastBreak = false;
		int lineCount = 0;
		while ((line = in.readLine()) != null) {
			if (line.startsWith("HTTP/1.") && lineCount == 0){
				String[] pieces = line.split(" ");
				if (pieces.length != 3)
					throw new IllegalStateException("HTTP header line had improper format.");
				responseData.put("Protocol", pieces[0]);
				responseData.put("ResponseCode", pieces[1]);
				responseData.put("ResponseMessage", pieces[2]);
			}
			else {
				pastBreak = true;
			}
			if (line.equals("")) {
					pastBreak = true;
			}
			else if (!pastBreak) {
				try {
					responseData.put(line.substring(0, line.indexOf(":")), line.substring(line.indexOf(":") + 1).trim());
				} catch (Exception e) {}
			}
			else {
				response.append(line + "\n");
			}
		}
		responseData.put("Body", response.toString());
		in.close();
	}
	
	private String getHeaders() {
		StringBuilder headerString = new StringBuilder();
		headerString.append("GET " + url.getFile() + " HTTP/1.0\r\n");
		headerString.append("User-Agent: cis455crawler");
		if (!checkHostHeader()) 
			headerString.append("Host: " + url.getHost() + "\r\n");
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
