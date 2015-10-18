package edu.upenn.cis455.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

import edu.upenn.cis455.crawler.info.URLInfo;

public class HttpClient {
	URLInfo urlInfo;
	HashMap<String, String> requestHeaders;
	HashMap<String, String> responseData;
	
	public HttpClient(String url) {
		requestHeaders = new HashMap<String, String>();
		responseData = new HashMap<String, String>();
		urlInfo = new URLInfo(url);
	}
	
	public void makeRequest() throws IOException {
		System.out.println("CONGRATS! It's working!");
		InetAddress ip = InetAddress.getByName(urlInfo.getHostName());
		Socket socket = new Socket(ip, urlInfo.getPortNo());
		String request = getHeaders();
		byte[] response = request.getBytes("UTF-8");
		OutputStream outStream = socket.getOutputStream();
		outStream.write(response,0,response.length);
		outStream.flush();
		readResponse(socket);
		socket.close();
	}
	
	private void readResponse(Socket socket) throws IOException {
		StringBuilder response = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String line;
		boolean pastBreak = false;
		while ((line = in.readLine()) != null) {
			if (line.startsWith("HTTP/1.")){
				String[] pieces = line.split(" ");
				if (pieces.length != 3)
					throw new IllegalStateException("HTTP header line had improper format.");
				responseData.put("Protocol", pieces[0]);
				responseData.put("ResponseCode", pieces[1]);
				responseData.put("ResponseMessage", pieces[2]);
			}
			else if (line.equals(""))
					pastBreak = true;
			else if (!pastBreak) {
				responseData.put(line.substring(0, line.indexOf(":")), line.substring(line.indexOf(":") + 1).trim());
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
		headerString.append("GET " + urlInfo.getFilePath() + " HTTP/1.0\r\n");
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
