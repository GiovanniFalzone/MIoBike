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
 * Servlet implementation class ChartDataServlet
 */
@WebServlet("/ChartDataServlet")
public class ChartDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChartDataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		DbManager db = new DbManager();
		
		JSONArray temp = null;
		JSONArray odo = null;
		JSONArray hum = null;
		JSONArray aq = null;
		
		try {
			temp = db.getLastSensorData("Temperature");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			odo = db.getLastSensorData("Odometer");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			hum = db.getLastSensorData("Humidity");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			aq = db.getLastSensorData("AirQuality");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject res = new JSONObject();
		res.put("Temperature", temp);
		res.put("Odometer", odo);
		res.put("Humidity", hum);
		res.put("AirQuality", aq);
		

		response.setContentType("json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(res.toString());
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
