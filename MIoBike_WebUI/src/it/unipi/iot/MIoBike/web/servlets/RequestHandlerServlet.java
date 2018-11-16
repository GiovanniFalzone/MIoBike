package it.unipi.iot.MIoBike.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import it.unipi.iot.MIoBike.MIoBike_Utils.Bike_Manager;

/**
 * Servlet implementation class RequestHandlerServlet
 */
@WebServlet("/RequestHandlerServlet")
public class RequestHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RequestHandlerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String sensorName = "GPS";
		
		Bike_Manager bm = new Bike_Manager();
		JSONArray js = bm.get_Bikes_Data();
		//iterare con for
		JSONObject bike = js.getJSONObject(0);
		String sensorData = bike.getString(sensorName);
		
		JSONObject GPS_data = new JSONObject(sensorData);
		String json = GPS_data.getString(("Bike1/GPS"));
		
		response.setContentType("application/json");
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
