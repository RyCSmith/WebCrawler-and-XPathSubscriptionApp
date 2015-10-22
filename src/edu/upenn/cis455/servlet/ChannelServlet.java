package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.URLData;

public class ChannelServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		DBWrapper database = null;
		try{
			String databasePath = getServletContext().getInitParameter("BDBstore");
			database = new DBWrapper(databasePath);
			database.openDB();
			HttpSession session = request.getSession();
			String username = (String) session.getAttribute("username");
			if (username == null)
				response.sendRedirect("/servlet/login");
			else {
				String channelName = request.getPathInfo();
				channelName = channelName.substring(1); //removes '/'
				HashMap<String, Set<URLData>> articles = database.getChannelArticles(channelName);
				StringBuilder builder = new StringBuilder();
				builder.append("<html><body>");
				for (String xpath : articles.keySet()) {
					builder.append("<div><b>XPath: " + xpath + " matched the following articles.</b></div><br><br>");
					Set<URLData> pathArticles = articles.get(xpath);
					for (URLData data : pathArticles) {
						builder.append("<div>URL: " + data.getUrl() + "</div><br>");
						builder.append("<div>" + data.getContent() + "</div><br><br>");
					}
					builder.append("<br><br>");
				}
				builder.append("</body></html>");
				sendResponse(response, builder.toString());
				database.closeDB();
			}
		} catch(Exception e) {
			e.printStackTrace();
			sendResponse(response, "<html><body><div>An error occurred while displaying "
					+ "this channel. Please try again.</div></body></html>");
		} finally {
			if (database != null)
				database.closeDB();
		}
	}
	
	private void sendResponse(HttpServletResponse response, String message) {
		response.setContentType("text/html");
	    response.setStatus(200);
	    response.setContentLength(message.length());
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println(message);
		} catch (IOException e) {
		}	
	}
}
