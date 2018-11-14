package it.unipi.iot.MIoBike.web.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(name = "login-servlet", urlPatterns = { "/LoginServlet" })
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
	public void init() throws ServletException {
		System.out.println("----------- initializing Login Servlet -----------");
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String data = "Hello World!";
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(data);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession current_session = request.getSession();
		response.setContentType("text/html");
		String url = "index.html";
		
		// Connect to Database
		DbManager db = new DbManager();

		//control blank fields
		if(request.getParameter("username") == null || request.getParameter("password") == null) {
			current_session.setAttribute("errorMessage", "You must fill each field");
			response.sendRedirect(url);
			return;
		}
		
		//get parameter from form
		String usr = request.getParameter("username");
		String pwd = request.getParameter("password");
		
		try {
			if(!db.authenticate(usr, pwd)) {
				current_session.setAttribute("errorMessage", "Authentication failed: username or password are wrong!");
				response.sendRedirect(url);
				return;
			}
		} catch (Exception e) {
			response.getWriter().write("Error while performing query.");
		}
		
		HashMap<String, Object> user = null;
		
		try {
			//login on database
			user = db.login(usr, pwd);
		} catch (Exception e) {
			response.getWriter().write("Error while performing query.");
		}
		
		//check if "admin" button was pressed and if the user is an admin
		if(request.getParameter("admin")!=null) {
			if((boolean)user.get("admin")) {
				//user is an admin and logged as admin
				request.getSession().setAttribute("admin", 1);
			} else {
				//response.getWriter().write("Authentication failed.");
				current_session.setAttribute("errorMessage", "Authentication failed: you're not an administrator!");
				response.sendRedirect(url);
				return;
			}
		}
		
		//set the current session with user data
		System.out.println("------------------- Logging in -------------------");
		current_session.setAttribute("userId", (int)user.get("ID_user"));
		current_session.setAttribute("username", user.get("username").toString());
		current_session.setAttribute("email",  user.get("email").toString());
		current_session.setAttribute("subscriptionId", (int)user.get("ID_subscription"));
		current_session.setAttribute("keyId", (int)user.get("ID_key"));
		current_session.setAttribute("weight", (double)user.get("weight"));
		current_session.setAttribute("avatar", user.get("avatar").toString());
		
		//redirect to dashboard
		response.sendRedirect("web-UI/dashboard.jsp");
		//RequestDispatcher dispatcher = request.getRequestDispatcher("web-UI/dashboard.jsp");
		//dispatcher.forward(request, response);
	}

}
