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
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import edu.upenn.cis455.crawler.info.URLInfo;

public class HttpsClient extends HttpClient {
	URL url;
	
	public HttpsClient(String url) throws MalformedURLException {
		super(url);
		this.url = new URL(url);
	}
	
	public void makeRequest() throws IOException {
	    HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
	    String request = getHeaders();
		byte[] response = request.getBytes("UTF-8");
	    connection.setUseCaches(false);
	    connection.setDoOutput(true);
	    connection.setRequestMethod("GET");

	    DataOutputStream outStream = new DataOutputStream (connection.getOutputStream());
	    outStream.write(response,0,response.length);
		outStream.flush();
		System.out.println(connection.getContentType());
		System.out.println(connection.getContentLengthLong());
		System.out.println(connection.getLastModified());
		Map<String,List<String>> map = connection.getHeaderFields();
		
			
		
		readResponse(connection.getInputStream());
	    outStream.close();
	}

}
