package it.unipi.iot.MIoBike.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import it.unipi.iot.MIoBike.web.DbManager;

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
		String data = "Ciao!";
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
		boolean admin = false;
		
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
			response.getWriter().write("Error while performing query for authentication");
		}
		
		JSONObject user = null;
		
		try {
			//login on database
			user = db.login(usr, pwd);
		} catch (Exception e) {
			response.getWriter().write("Error while performing query for login");
		}
		
		//check if "admin" button was pressed and if the user is an admin
		if(request.getParameter("admin")!=null) {
			if(user.getBoolean("admin")) {
				//user is an admin and logged as admin
				admin = true;
			} else {
				//response.getWriter().write("Authentication failed.");
				current_session.setAttribute("errorMessage", "Authentication failed: you're not an administrator!");
				response.sendRedirect(url);
				return;
			}
		}
		
		//set the current session with user data
		current_session.setAttribute("userId", user.getInt("ID_user"));
		current_session.setAttribute("username", user.getString("username"));
		if(user.has("email"))
			current_session.setAttribute("email",  user.getString("email"));
		else
			current_session.setAttribute("email",  null);
		
		//redirect to dashboard
		//if admin and log as admin redirect to admin dashboard
		if(admin) {
			response.sendRedirect("web-UI/admin-dashboard.jsp");
			return;
		}
		
		current_session.setAttribute("subscriptionId", user.getInt("ID_subscription"));
		current_session.setAttribute("weight", user.getDouble("weight"));
		current_session.setAttribute("avatar", user.getString("avatar"));
		current_session.setAttribute("balance", user.getDouble("balance"));
		current_session.setAttribute("errorMessage", null);

		response.sendRedirect("web-UI/dashboard.jsp");
		//RequestDispatcher dispatcher = request.getRequestDispatcher("web-UI/dashboard.jsp");
		//dispatcher.forward(request, response);
	}

}
