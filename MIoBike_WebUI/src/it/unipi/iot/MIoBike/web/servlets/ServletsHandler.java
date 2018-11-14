package it.unipi.iot.MIoBike.web.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletsHandler {
	
	public void redirect(HttpServletRequest req, HttpServletResponse resp, String url, String err) throws IOException{
		
		if(err != null)
			req.getSession().setAttribute("errorMessage", err);
		resp.setContentType("text/html");
		resp.sendRedirect(url);
	}
}
