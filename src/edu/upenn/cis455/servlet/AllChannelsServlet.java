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

public class AllChannelsServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		DBWrapper database = null;
		try{
			String databasePath = getServletContext().getInitParameter("BDBstore");
			database = new DBWrapper(databasePath);
			database.openDB();
			HashMap<String, Set<URLData>> channelsData = database.getAllChannelsContent();
			StringBuilder builder = new StringBuilder();
			builder.append("<html><body>");
			for (String channel : channelsData.keySet()) {
				builder.append("<div>Channel: " + channel + ", contains the following articles.</div><br><br>");
				Set<URLData> channelArticles = channelsData.get(channel);
				for (URLData data : channelArticles) {
					builder.append("<div>URL: " + data.getUrl() + "</div><br>");
					builder.append("<div>" + data.getContent() + "</div><br><br>");
				}
				builder.append("<br><br>");
			}
			builder.append("</body></html>");
			sendResponse(response, builder.toString());
			
		} catch(Exception e) {
			e.printStackTrace();
			sendResponse(response, "<html><body><div>An error occurred while displaying "
					+ "all channels. Please try again.</div></body></html>");
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
