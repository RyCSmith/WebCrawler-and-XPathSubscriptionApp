package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sleepycat.je.Database;

import edu.upenn.cis455.storage.DBWrapper;

public class LoginServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String loginForm = "<html>" +
				"	<body>" +
				"		<center>" +
				"		<div>Login</div><br><div>" +
				"			<form action=\"/servlet/login\" method=\"post\">" +
				"			  Username:<br>" +
				"			  <input type=\"text\" name=\"username\" style=\"width:300px;\"><br>" +
				"			  Password:<br>" +
				"			  <input type=\"text\" name=\"password\" style=\"width:300px;\"><br>" +
				"			  <br>" +
				"			  <input type=\"submit\" value=\"Submit\" style=\"width:100px;\">" +
				"			</form>" +
				"		</div><br><div>(If a user is currently logged in on this machine, then will be logged "
				+ "out and you will be logged in.)</div>"
				+ "</center>" +
				"	</body></html>";
		sendResponse(response, loginForm);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		DBWrapper database = null;
		try {
			String databasePath = getServletContext().getInitParameter("BDBstore");
			database = new DBWrapper(databasePath);
			database.openDB();
			String username = request.getParameter("username").trim();
			String password = request.getParameter("password").trim();
			if (username.equals("") || password.equals("")) {
				String message = "<html><body><div>Either username or password was not included in the request."
						+ " Please go back and try again.</div><body><html>";
				sendResponse(response, message);
			}
			else if (!database.checkUserExists(username)) {
				String message = "<html><body><div>The provided username does not exist."
						+ " Please go back and try again.</div></body></html>";
				sendResponse(response, message);
			}
			else if (!database.checkPassword(username, password)) {
				String message = "<html><body><div>Incorrect password. Please go back and try again.</div></body></html>";
				sendResponse(response, message);
			}
			else {
				HttpSession session = request.getSession();
				session.setAttribute("username", username);
				
				try {
					response.sendRedirect("/servlet/account");
				} catch (IOException e) {
				}
			}
			database.closeDB();
		}catch (Exception e) {
			e.printStackTrace();
			String message = "<html><body><div>An error occurred while processing your login request. Please try again.</div></body></html>";
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
