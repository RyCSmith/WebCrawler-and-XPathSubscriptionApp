package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HomeServlet extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String welcomeMessage = "<html><body><center><div>Welcome to the XPath Channel site!</div><br>"
				+ "<div>What do you want to do?</div><br><div><a href=\"/servlet/createAccount\">Create Account</a><br>"
				+ "<a href=\"/servlet/login\">Log in</a><br><a href=\"/servlet/allChannels\">See all channels</a><br>"
				+ "<a href=\"/servlet/account\">My Account</a><br>"
				+ "</div></center></body></html>";
		response.setContentType("text/html");
	    response.setStatus(200);
	    response.setContentLength(welcomeMessage.length());
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println(welcomeMessage);
		} catch (IOException e) {
		}	
	}
}
