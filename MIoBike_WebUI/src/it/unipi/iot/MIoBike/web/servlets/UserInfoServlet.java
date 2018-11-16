package it.unipi.iot.MIoBike.web.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

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
		
		int user_ID = Integer.parseInt(request.getParameter("user_ID"));
		
		JSONArray json_arr = new JSONArray();
		JSONObject json_container = new JSONObject();
		JSONObject json_obj;
		HashMap<String, Object> res = new HashMap<String, Object>();
		
		/*try {
			System.out.println("add a trip");
			boolean r = db.addTrip(1,5,"2018-11-20 10:04:04", "2018-11-20 10:12:02", 43.3214, 10.3231, 43.3224, 10.3231, 2);
		} catch (Exception e) {
			response.getWriter().write("Error while performing query to retrieve subscription info");
			return;
		}*/
		
		//get user stats from stat table
		try {
			res = db.getUserInfo(user_ID);
		} catch (Exception e) {
			response.getWriter().write("Error while performing query to retrieve user info");
			return;
		}
		
		json_obj = new JSONObject();
	
		if(res!=null) {
			json_obj.put("ID_stat", res.get("ID_stat").toString());
			json_obj.put("trips", res.get("trips").toString());
			json_obj.put("tot_km", res.get("tot_km").toString());
			json_obj.put("tot_cal", res.get("tot_cal").toString());
			json_obj.put("fav_bike", res.get("fav_bike").toString());
			json_obj.put("avg_km", res.get("avg_km").toString());
			json_obj.put("avg_cal", res.get("avg_cal").toString());
			json_obj.put("avg_speed", res.get("avg_speed").toString());
			json_obj.put("max_km", res.get("max_km").toString());
			json_obj.put("max_speed", res.get("max_speed").toString());
		} else {
			json_obj = null;
		}
		
		json_container.put("user_statistics",json_obj);
		
		//get user subscription info
		try {
			res = db.getSubscription(user_ID);
		} catch (Exception e) {
			response.getWriter().write("Error while performing query to retrieve subscription info");
			return;
		}
		
		json_obj = new JSONObject();
		if(res!=null) {
			json_obj.put("activation_date", res.get("activation_date").toString());
			json_obj.put("expiration_date", res.get("expiration_date").toString());
			json_obj.put("balance", res.get("balance").toString());
		} else {
			json_obj = null;
		}
		
		json_container.put("subscription_info",json_obj);
		
		//get user's trips
		try {
			res = db.getTrips(user_ID);
		} catch (Exception e) {
			response.getWriter().write("Error while performing query to retrieve trips");
			return;
		}
		
		if(res!=null) {
			json_obj = new JSONObject();
			json_obj.put("total_count", res.get("count").toString());
			json_obj.put("total_km", res.get("tot_km").toString());
			json_obj.put("total_cal", res.get("tot_cal").toString());
			json_container.put("trips",json_obj);
			
			try {
				res = db.getDailyTrips(user_ID);
			} catch (Exception e) {
				response.getWriter().write("Error while performing query to retrieve daily trips");
				return;
			}
			if(res!=null) {
				json_obj = new JSONObject();
				json_obj.put("daily_count", res.get("count").toString());
				json_obj.put("daily_km", res.get("tot_km").toString());
				json_obj.put("daily_cal", res.get("tot_cal").toString());
				json_container.put("daily_trips",json_obj);
			}
		} else {
			json_obj = null;
		}
			
		
		String json = json_container.toString();
		
		response.setContentType("text/plain");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
