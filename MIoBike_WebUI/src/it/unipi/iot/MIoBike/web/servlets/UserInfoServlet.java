package it.unipi.iot.MIoBike.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import it.unipi.iot.MIoBike.MIoBike_BikeManager.BikeManager;
import it.unipi.iot.MIoBike.web.DbManager;

import static it.unipi.iot.MIoBike.web.Constants_web.*;

/**
 * Servlet implementation class UserInfoServlet
 */
@WebServlet("/UserInfoServlet")
public class UserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserInfoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    

    @Override
   	public void init() throws ServletException {
   		System.out.println("----------- initializing User Servlet ------------");
   	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub// TODO Auto-generated method stub
		DbManager db = new DbManager();
		BikeManager bm = new BikeManager();
		
		int userId = 0;
		if(request.getParameter("user_ID") != null)
			userId = Integer.parseInt(request.getParameter("user_ID"));
		String username = request.getParameter("username").replaceAll(" ", "");
		
		JSONObject json_container = new JSONObject();
		JSONObject json_obj;
		
		//get user subscription info
		try {
			json_obj = db.getSubscription(userId);
		} catch (Exception e) {
			response.getWriter().write("Error while performing query to retrieve subscription info");
			return;
		}
		
		json_container.put("subscription_info",json_obj);

		double user_speed = 0;
		
		try {
			user_speed = db.getSpeed(username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		user_speed = Math.round(user_speed * 100.0) / 100.0;
		
		JSONObject trips = new JSONObject();
		try {
			trips = db.getTrips(username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String bikeName = null;
		//JSONArray user_gps = null;
		if(CONNECTED) 
			bikeName = bm.retrieve_BikeName_by_UserName(username);
		//retrieve all data for a certain user
		//json_obj = new JSONObject("{user: "+userId+", trips: 10, kilometers: 20, speed: 12.3}");
		JSONObject json = new JSONObject();
		json.put("trips", trips.getInt("trips"));
		json.put("speed", user_speed);
		json.put("distance", trips.getInt("kilometers"));
		json.put("time", trips.getDouble("time"));
		json_container.put("trips_info", json);
		json_container.put("using_bike", bikeName);
		
		response.setContentType("text/plain");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json_container.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		DbManager db = new DbManager();

		String userId = request.getParameter("userId");
		double weight = 0;
		String email = null;
		System.out.println(request);
		if(!(request.getParameter("weight").isEmpty()))
			weight = Double.parseDouble(request.getParameter("weight"));
		if(!(request.getParameter("email").isEmpty()))
			email = request.getParameter("email");
		
		if(weight == 0 && email == null) {
			response.getWriter().write("You must fullfill at least one of the fields");
			return;
		}
		
		try {
			db.updateUserInfo(userId, weight, email);
		} catch (Exception e) {
			response.getWriter().write("Error while performing query to update user balance");
			return;
		}
		
		HttpSession session = request.getSession(true);
		if(weight != 0)
			session.setAttribute("weight", weight);
		if(email != null)
			session.setAttribute("email", email);
		response.setContentType("text/plain");
	    response.setCharacterEncoding("UTF-8");
	    response.sendRedirect("web-UI/dashboard.jsp");
	}

}
