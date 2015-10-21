package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AccountServlet extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		String tempResponse = "<html><body<div>Username: " + username + "</div></body></html>";
		response.setContentType("text/html");
	    response.setStatus(200);
	    response.setContentLength(tempResponse.length());
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println(tempResponse);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
