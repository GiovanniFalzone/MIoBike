package it.unipi.iot.MIoBike.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import it.unipi.iot.MIoBike.web.DbManager;

/**
 * Servlet implementation class DbManagerRequestServlet
 */
@WebServlet("/DbManagerRequestServlet")
public class DbManagerRequestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DbManagerRequestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		DbManager db = new DbManager();
		String bikeName = request.getParameter("BikeName");
		String resName = request.getParameter("ResourceName");
		String date = request.getParameter("Date");
		String value = request.getParameter("value");
		String user = request.getParameter("UserId");
	   
		//String user = request.getParameter("UserId");
		resName = resName.replaceAll(" ", "");
		boolean res = false;
		if(resName == "Start_Trip") {
			// add a trip to trips table
			JSONArray json = new JSONArray();
			try {
				json = db.getSensorByBike("Odometer", bikeName);
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
		} else if(resName == "End_Trip") {
			// update the last "Start_Trip" entry of trips table
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
		} else {
			try {
				res = db.updateSensors(resName, bikeName, value, date, user);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Risultato:"+res);
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
