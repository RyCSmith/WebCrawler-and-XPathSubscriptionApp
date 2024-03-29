package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DBWrapper;

@SuppressWarnings("serial")
public class CreateAccountServlet extends HttpServlet{
	
		@Override
		public void doGet(HttpServletRequest request, HttpServletResponse response) {
			String signupForm = "<html>" +
					"	<body>" +
					"		<center>" +
					"		<div>Create Account</div><br><div>" +
					"			<form action=\"/servlet/createAccount\" method=\"post\">" +
					"			  Username:<br>" +
					"			  <input type=\"text\" name=\"username\" style=\"width:300px;\"><br>" +
					"			  Password:<br>" +
					"			  <input type=\"text\" name=\"password\" style=\"width:300px;\"><br>" +
					"			  <br>" +
					"			  First Name:<br>" +
					"			  <input type=\"text\" name=\"firstname\" style=\"width:300px;\"><br>" +
					"			  <br>" +
					"			  Last Name:<br>" +
					"			  <input type=\"text\" name=\"lastname\" style=\"width:300px;\"><br>" +
					"			  <br>" +
					"			  <input type=\"submit\" value=\"Submit\" style=\"width:100px;\">" +
					"			</form>" +
					"		</div></center>" +
					"	</body></html>";
					
			String loggedInMessage = "<html><body><div>You are currently logged in. \n"
					+ "You must <a href=\"/servlet/logout\">Logout</a> if you would"
					+ " like to create a new account.";
			HttpSession session = request.getSession(false);
			if (session == null)
				sendResponse(response, signupForm);
			else {
				String username = (String) session.getAttribute("username");
				if (username == null) {
					sendResponse(response, signupForm);
				}
				else {
					sendResponse(response, loggedInMessage);
				}
			}
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
				if (database.checkUserExists(username)) {
					String alreadyExistsResponse = "<html><body><div>Sorry, that username is already in use. "
							+ "Please go back and choose another. </div></body></html>";
					sendResponse(response, alreadyExistsResponse);
				}
				else if (username.equals("") || password.equals("")) {
					String missingFieldResponse = "<html><body><div>Username or password were absent in your"
							+ " request. Please try again. </div></body></html>";
					sendResponse(response, missingFieldResponse);
				}
				else {
					if ((request.getParameter("firstname") != null) && 
							(request.getParameter("lastName") != null)) {
						database.addUser(username, password, request.getParameter("firstname"), request.getParameter("lastName"));
					}
					else {
						database.addUser(username, password);
					}
					HttpSession session = request.getSession();
					session.setAttribute("username", username);
					
					try {
						response.sendRedirect("/servlet/account");
					} catch (IOException e) {
					}
				}
				database.closeDB();
			} catch (Exception e) {
				String message = "<html><body><div>An error occurred while attempting to create"
						+ " an account with those credentials. Please attempt with a different username and password.</div></body></html>";
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