package com.tweetcloud.services.authenticate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Signout server signs the user out.
 * 
 * @author Notme.
 */
public class SignoutServlet extends HttpServlet {

	private static final long serialVersionUID =  0L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getSession().invalidate();
		response.sendRedirect(request.getContextPath() + "/");
	}
}
