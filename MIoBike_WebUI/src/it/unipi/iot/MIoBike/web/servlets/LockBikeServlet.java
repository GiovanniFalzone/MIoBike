package it.unipi.iot.MIoBike.web.servlets;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import it.unipi.iot.MIoBike.MIoBike_BikeManager.BikeManager;
import it.unipi.iot.MIoBike.web.DbManager;

import static it.unipi.iot.MIoBike.web.Constants_web.*;

/**
 * Servlet implementation class LockBikeServlet
 * Receives post from user to unlock a locked bike
 */
@WebServlet("/LockBikeServlet")
public class LockBikeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LockBikeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String user = request.getParameter("user");
		String bike = request.getParameter("bike");
		
		BikeManager bm = new BikeManager();
		boolean res = false;
		if(CONNECTED)
			res = bm.User_Unlock_Request(user, bike);
		
		JSONObject json = new JSONObject();
		json.put("res", res);
		response.setContentType("json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String user = request.getParameter("user");
		String bike = request.getParameter("bike").toString();
		String req = request.getParameter("unlock");
		
		BikeManager bm = new BikeManager();
		DbManager db = new DbManager();
		boolean res = false;
		if(req.equals("true")) {
			res = bm.User_Unlock_Request(user, bike);
			if(res) {
				JSONArray json = new JSONArray();
				try {
					json = db.getSensorByBike("Odometer", bike);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String odo = json.getJSONObject(0).getString("value");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date today_date = new Date();
				String today = sdf.format(today_date);
				
				try {
					res = db.insertTrip("Start_Trip", bike, odo, today, user);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			bm.User_Release_Bike(bike);
			JSONArray json = new JSONArray();
			try {
				json = db.getSensorByBike("Odometer", bike);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String odo = json.getJSONObject(0).getString("value");
			
			JSONObject start = new JSONObject();
			try {
				start = db.getLastTrip("Start_Trip", user);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(start == null)
				return;
			
			
			// Calculate the differences between start and end, and save in table in column time
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = start.getString("date");
			Date old_date = null;
			try {
				old_date = sdf.parse(date);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Date today_date = new Date();			
			// Calculates the difference in milliseconds.
			long millisDiff = today_date.getTime() - old_date.getTime();
			date = " "+millisDiff;
			System.out.println("Porco"+date);
			
			String start_value = start.getString("value");
			int start_v = Integer.parseInt(start_value);
			int end_v = Integer.parseInt(odo);
			int new_value = end_v - start_v;
			double new_balance = new_value * 0.10;
			String kilometers = ""+new_value;
			
			int id = start.getInt("id_entry");
			
			try {
				res = db.updateTrip("End_Trip", bike, kilometers, date, user, id);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				res = db.decreaseBalance(user, new_balance);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		JSONObject json = new JSONObject();
		json.put("res", res);
		response.setContentType("json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json.toString());
	}

}
