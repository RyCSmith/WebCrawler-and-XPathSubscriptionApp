package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DBWrapper;

public class AccountServlet extends HttpServlet {
	
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
				StringBuilder accountDisplay = new StringBuilder();
				accountDisplay.append("<html><body<div>You are logged in as " + username + ".</div><br>");
				accountDisplay.append("<div><a href=\"/servlet/logout\">Logout</a></div><br>");
				accountDisplay.append("<div><a href=\"/servlet/home\">Home</a></div><br>");
				accountDisplay.append("<div><a href=\"/servlet/createChannel\">Create New Channel</a></div><br>");
				accountDisplay.append("<div><ul>");
				Set<String> channels = database.getUserChannels(username);
				for (String channel : channels) {
					accountDisplay.append("<li><a href=\"/servlet/channel/" + channel + "\">" + channel + "</a>");
					accountDisplay.append("<form action=\"/servlet/delete\" method=\"post\"><button name=\"delete\" value=\"" 
							+ channel +"\">Delete</button></form>");
				}
				accountDisplay.append("</ul></div></body></html>");
				sendResponse(response, accountDisplay.toString());
				database.closeDB();
			}
		} catch(Exception e) {
			e.printStackTrace();
			sendResponse(response, "<html><body><div>An error occurred while displaying "
					+ "your account. Please try again.</div></body></html>");
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
