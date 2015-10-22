package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.xpathengine.DocumentServices;

public class CreateChannelServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			HttpSession session = request.getSession(false);
			if (session == null)
				response.sendRedirect("/servlet/login");
			String username = (String) session.getAttribute("username");
			if (username == null)
				response.sendRedirect("/servlet/login");
			String createForm = 
					"<html>" +
					"	<head>" +
					"		<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>"+
					"	</head>" +
					"	<body>" +
					"		<center>" +
					"		<div>Create Channel</div><br><div>" +
					"			<form action=\"/servlet/createChannel\" method=\"post\">" +
					"			  Channel Name:<br>" +
					"			  <input type=\"text\" name=\"channelName\" style=\"width:300px;\">" +
					"			  <br><br>" +
					"			  XPath:<br>" +
					"			<div id=\"emptydiv\"value=\"0\">" +
					"			  <input type=\"text\" name=\"xpath0\" style=\"width:300px;\">" +
					"			  <br></div>" +
					"			  <button type=\"button\" id=\"add-button\">Add Another XPath</button>" +
					"			  <input type=\"submit\" value=\"Submit\" style=\"width:100px;\">" +
					"			</form></div>" +
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
			sendResponse(response, createForm);
		} catch (Exception e) {
			e.printStackTrace();
			String message = "<html><body><div>An error occurred while attempting to create"
					+ " your channel. Please try again.</div></body></html>";
			sendResponse(response, message);
		}
	}
	
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
			String[] xpaths = DocumentServices.getXPathsFromPost(request);
			String channelName = request.getParameter("channelName");
			database.addChannel(username, channelName, xpaths);
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
