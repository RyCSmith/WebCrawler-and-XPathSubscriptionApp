package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.xpathengine.DocumentServices;

public class DeleteChannelServlet extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		DBWrapper database = null;
		try {
			String databasePath = getServletContext().getInitParameter("BDBstore");
			database = new DBWrapper(databasePath);
			database.openDB();
			//this stuff might throw null pointers which will be caught below.
			HttpSession session = request.getSession(false);
			String username = (String) session.getAttribute("username");
			String channelName = request.getParameter("delete");
			database.deleteChannel(username, channelName);
			response.sendRedirect("/servlet/account");
		} catch (Exception e) {
			e.printStackTrace();
			String message = "<html><body><div>An error occurred while attempting to create"
					+ " your channel. Please try again.</div></body></html>";
			sendResponse(response, message);
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
