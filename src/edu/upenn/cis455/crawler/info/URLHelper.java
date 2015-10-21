package edu.upenn.cis455.crawler.info;

public class URLHelper {
	public static enum Protocol {HTTP, HTTPS, UNKNOWN};
	
    public static String extractDomain(String url) {
    	if (url.startsWith("http://"))
    		url = url.substring(7);
    	else if (url.startsWith("https://"))
    		url = url.substring(8);
    	if (url.startsWith("www."))
    		url = url.substring(4);
    	if (url.indexOf('/') >= 0)
    		url = url.substring(0, url.indexOf('/'));
    	return url;
    }
    
    public static String removeFilePath(String url) {
    	String original = url;
    	int index = 0;
    	if (url.startsWith("http://")){
    		url = url.substring(7);
    		index += 7;
    	}
    	else if (url.startsWith("https://")){
    		url = url.substring(8);
    		index += 8;
    	}
    	if (url.startsWith("www.")){
    		url = url.substring(4);
    		index += 4;
    	}
    	if (url.indexOf('/') >= 0) {
    		index += url.indexOf('/');
    		url = url.substring(0, url.indexOf('/'));
    	}
    	return original.substring(0, index);
    }
    
    public static Protocol getProtocol(String url) {
    	if (url.startsWith("http://"))
    		return Protocol.HTTP;
    	else if (url.startsWith("https://"))
    		return Protocol.HTTPS;
    	else
    		return Protocol.UNKNOWN;
    }		
}
