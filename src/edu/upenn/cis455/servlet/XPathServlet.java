package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Map;

import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.upenn.cis455.httpclient.HttpClient;
import edu.upenn.cis455.xpathengine.XPathEngineFactory;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;

@SuppressWarnings("serial")
public class XPathServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String basicTemplate = 
				"<html>" +
				"	<head>" +
				"		<title>XPath Evaluator</title>" +
				"<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>"+
				"	</head>" +
				"	<body>" +
				"		<div>" +
				"			<span>Ryan Smith</span>" +
				"			<br>" +
				"			<span>rysmit</span>" +
				"		</div>" +
				"		<center>" +
				"			<form action=\"/servlet/xpath\" method=\"post\">" +
				"			  XPath:<br>" +
				"			<div id=\"emptydiv\"value=\"0\">" +
				"			  <input type=\"text\" name=\"xpath0\" style=\"width:300px;\">" +
				"			  <br>" +
				"			</div>  Document URL:<br>" +
				"			  <input type=\"text\" name=\"url\" style=\"width:300px;\">" +
				"			  <br><br>" +
				"			  <button type=\"button\" id=\"add-button\">Add Another XPath</button>" +
				"			  <input type=\"submit\" value=\"Submit\" style=\"width:100px;\">" +
				"			</form>" +
				"		</center>" +
				"	</body>" +
				"	<script>"+
				"		$('#add-button').click(function() {"+
				"			var indexText = document.getElementById('emptydiv').getAttribute('value');"+
				"			var nextIndex = parseInt(indexText) + 1;"+
				"			document.getElementById('emptydiv').setAttribute('value', nextIndex);"+
				"			var newHTML = \"<input type=\\\"text\\\" name=\\\"xpath\" + nextIndex.toString() + \"\\\" style=\\\"width:300px;\\\"><br>\";"+
				"			$('#emptydiv').append(newHTML);"+
				"		});"+
				"	</script>"+
				"</html>";
		response.setContentType("text/html");
	    response.setStatus(200);
	    response.setContentLength(basicTemplate.length());
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println(basicTemplate);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			String url = request.getParameter("url");
			if (url != null && !url.equals("")) {
				Document domDoc;
				if (ServletSupport.checkIsLocal(url)){
					domDoc = ServletSupport.buildDocument(url, true);
				} 
				else {
					HttpClient client = new HttpClient(request.getParameter("url"));
					client.makeRequest();
					domDoc = ServletSupport.buildDocument(client.getDocument(), false);
				}
				if (domDoc == null)
					throw new IllegalStateException();
				XPathEngineImpl xPathEngine = (XPathEngineImpl) XPathEngineFactory.getXPathEngine();
				String[] xpaths = ServletSupport.getXPathsFromPost(request);
				xPathEngine.setXPaths(xpaths);
				boolean[] results = xPathEngine.evaluate(domDoc);
				
				StringBuilder html = new StringBuilder("<html><body><div>RESULTS</div><br><div><ul>");
				for (int i = 0; i < results.length; i++) {
					html.append("<li>");
					html.append(xpaths[i] + " : " + results[i]);
					html.append("</li>");
				}
				html.append("</ul></div></body></html>");
				
				response.setContentType("text/html");
			    response.setStatus(200);
			    response.setContentLength(html.length());
				PrintWriter out;
				out = response.getWriter();
				out.println(html);
			}
			else {
				String html = "<html><body><div>You did not provide a valid URL. Please try again.</div></body></html>";
				response.setContentType("text/html");
			    response.setStatus(200);
			    response.setContentLength(html.length());
				PrintWriter out;
				out = response.getWriter();
				out.println(html);
			}
		} catch (IllegalStateException eState) {
			String html = "<html><body><div>Error parsing document.</div></body></html>";
			response.setContentType("text/html");
		    response.setStatus(200);
		    response.setContentLength(html.length());
			PrintWriter out;
			try {
				out = response.getWriter();
				out.println(html);
			} catch (IOException e){}
		}
		catch (Exception e) {
			StringBuilder html = new StringBuilder("<html><body><div>ERROR: </div><br><div>");
			html.append(e.getMessage());
			html.append("</div></body></html>");
			response.setContentType("text/html");
		    response.setStatus(200);
		    response.setContentLength(html.length());
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e1) {}
			out.println(html);
		}
	}
}









