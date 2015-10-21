package edu.upenn.cis455.httpclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import edu.upenn.cis455.crawler.info.URLInfo;

public class HttpsClient extends HttpClient {
	URL url;
	long contentLength;
	long lastModified;
	public HttpsClient(String url, String method) throws MalformedURLException {
		super(url, method);
		this.url = new URL(url);
	}
	
	public void makeRequest() throws IOException {
	    HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
	    connection.setUseCaches(false);
	    connection.setDoOutput(true);
	    connection.setRequestMethod(method);
	    addHeaders(connection);
		String c = connection.getContentType();
		responseData.put("Content-Type", connection.getContentType());
		contentLength = connection.getContentLengthLong();
		lastModified = connection.getLastModified();
		readResponse(connection.getInputStream());
	}
	
	protected void addHeaders(HttpsURLConnection connection) {
		connection.addRequestProperty("User-Agent", "cis455crawler");
		for (String key : requestHeaders.keySet()) {
			connection.addRequestProperty(key, requestHeaders.get(key));
		}
	}
	
	@Override
	protected void readResponse(InputStream inStream) throws IOException {
		StringBuilder response = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
		String line;
		while ((line = in.readLine()) != null) {
				response.append(line + "\n");
		}
		responseData.put("Body", response.toString());
		inStream.close();
	}
}
