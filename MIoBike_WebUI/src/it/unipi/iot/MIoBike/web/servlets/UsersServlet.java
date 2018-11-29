package it.unipi.iot.MIoBike.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import it.unipi.iot.MIoBike.web.DbManager;

/**
 * Servlet implementation class UsersServlet
 */
@WebServlet("/UsersServlet")
public class UsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UsersServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		DbManager db = new DbManager();
		
		JSONArray users = new JSONArray();
		try {
			users = db.getUsers();
		} catch (Exception e) {
			response.getWriter().write("Error while performing query to retrieve users info");
			return;
		}

		for (int i=0; i < users.length(); i++) {
		    JSONObject user = users.getJSONObject(i);
			JSONObject subs = new JSONObject();
		    try {
				subs = db.getSubscription(user.getInt("ID"));
			} catch (Exception e) {
				response.getWriter().write("Error while performing query to retrieve subscription info for an user");
				return;
			}
		    user.put("subscription_info", subs);
		}
		
		response.setContentType("text/plain");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(users.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
