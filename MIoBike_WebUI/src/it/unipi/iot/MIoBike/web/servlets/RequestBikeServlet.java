package it.unipi.iot.MIoBike.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;

import it.unipi.iot.MIoBike.MIoBike_BikeManager.BikeManager;

import static it.unipi.iot.MIoBike.web.Constants_web.*;

/**
 * Servlet implementation class RequestBikeServlet
 */
@WebServlet(name = "request-servlet", urlPatterns = { "/RequestBikeServlet"})
public class RequestBikeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RequestBikeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String userType = request.getParameter("userType");
		System.out.println("User type: "+userType);
		BikeManager bm;
		bm = new BikeManager();
		JSONArray json_array = new JSONArray();
		String bikeArray = null;
		if(userType.equals("1")){
			if(CONNECTED)
				json_array = bm.get_Bikes_Data();
			else {
				bikeArray = "[{'BikeName':'Bike2','Resources':{'Speed':{},'Temperature':{},'TyrePressure':{},'User':{},'AirQuality':{},'Humidity':{},'NFC':{},'Odometer':{},'Sampling_Period':{},'GPS':{'ResourceData':{'format':'DD','value':{'lat':43.7158,'long':10.4000}},'ResourceName':'GPS','BikeName':'Bike1','Date':'2018.11.18_18:27:13'},'Lock':{'ResourceData':{'format':'boolean','value':'true'},'ResourceName':'Lock','BikeName':'Lock','Date':'2018.11.19_10:29:22'}}},{'BikeName':'Bike3','Resources':{'Speed':{'ResourceData':{'format':'m/s','value':3.4},'ResourceName':'Speed','BikeName':'Bike1','Date':'2018.11.18_18:26:19'},'Temperature':{'ResourceData':{'format':'C','value':22.3},'ResourceName':'Temperature','BikeName':'Bike1','Date':'2018.11.18_18:27:15'},'TyrePressure':{'ResourceData':{'format':'bar','value':1.3},'ResourceName':'TyrePressure','BikeName':'Bike1','Date':'2018.11.18_18:26:17'},'User':{},'AirQuality':{},'Humidity':{'ResourceData':{'format':'%','value':80.5},'ResourceName':'Humidity','BikeName':'Bike1','Date':'2018.11.18_18:27:16'},'Odometer':{'ResourceData':{'format':'Km','value':12.6},'ResourceName':'Odometer','BikeName':'Bike1','Date':'2018.11.18_18:26:18'},'NFC':{},'Sampling_Period':{},'GPS':{'ResourceData':{'format':'DD','value':{'lat':43.7167,'long':10.3956}},'ResourceName':'GPS','BikeName':'Bike1','Date':'2018.11.18_18:27:13'},'Lock':{'ResourceData':{'format':'boolean','value':'true'},'ResourceName':'Lock','BikeName':'Lock','Date':'2018.11.19_10:29:22'}}},{'BikeName':'Bike4','Resources':{'Speed':{'ResourceData':{'format':'m/s','value':2.4},'ResourceName':'Speed','BikeName':'Bike1','Date':'2018.11.18_18:26:19'},'Temperature':{'ResourceData':{'format':'C','value':22.3},'ResourceName':'Temperature','BikeName':'Bike1','Date':'2018.11.18_18:27:15'},'TyrePressure':{'ResourceData':{'format':'bar','value':1.3},'ResourceName':'TyrePressure','BikeName':'Bike1','Date':'2018.11.18_18:26:17'},'User':{},'AirQuality':{},'Humidity':{'ResourceData':{'format':'%','value':80.5},'ResourceName':'Humidity','BikeName':'Bike1','Date':'2018.11.18_18:27:16'},'Odometer':{'ResourceData':{'format':'Km','value':12.6},'ResourceName':'Odometer','BikeName':'Bike1','Date':'2018.11.18_18:26:18'},'NFC':{},'Sampling_Period':{},'GPS':{'ResourceData':{'format':'DD','value':{'lat':43.7141,'long':10.3977}},'ResourceName':'GPS','BikeName':'Bike1','Date':'2018.11.18_18:27:13'},'Lock':{'ResourceData':{'format':'boolean','value':'false'},'ResourceName':'Lock','BikeName':'Lock','Date':'2018.11.19_10:29:22'}}},{'BikeName':'Bike1','Resources':{'Speed':{'ResourceData':{'format':'m/s','value':5.4},'ResourceName':'Speed','BikeName':'Bike1','Date':'2018.11.18_18:26:19'},'Temperature':{'ResourceData':{'format':'C','value':22.3},'ResourceName':'Temperature','BikeName':'Bike1','Date':'2018.11.18_18:27:15'},'TyrePressure':{'ResourceData':{'format':'bar','value':1.3},'ResourceName':'TyrePressure','BikeName':'Bike1','Date':'2018.11.18_18:26:17'},'User':{},'AirQuality':{},'Humidity':{'ResourceData':{'format':'%','value':80.5},'ResourceName':'Humidity','BikeName':'Bike1','Date':'2018.11.18_18:27:16'},'Odometer':{'ResourceData':{'format':'Km','value':12.6},'ResourceName':'Odometer','BikeName':'Bike1','Date':'2018.11.18_18:26:18'},'NFC':{},'Sampling_Period':{},'GPS':{'ResourceData':{'format':'DD','value':{'lat':43.7158,'long':10.3987}},'ResourceName':'GPS','BikeName':'Bike1','Date':'2018.11.18_18:27:13'},'Lock':{'ResourceData':{'format':'boolean','value':'false'},'ResourceName':'Lock','BikeName':'Lock','Date':'2018.11.19_10:29:22'}}}];";
				json_array = new JSONArray(bikeArray);
			}
			
			if(DEV_MOD)
				System.out.println(json_array.toString());
		} else {
			
			if(CONNECTED)
				json_array = bm.get_locked_Bikes();
			else {
				bikeArray = "[{'BikeName':'Bike2','Resources':{'Speed':{},'Temperature':{},'TyrePressure':{},'User':{},'AirQuality':{},'Humidity':{},'NFC':{},'Odometer':{},'Sampling_Period':{},'GPS':{'ResourceData':{'format':'DD','value':{'lat':43.7158,'long':10.4000}},'ResourceName':'GPS','BikeName':'Bike1','Date':'2018.11.18_18:27:13'},'Lock':{'ResourceData':{'format':'boolean','value':'true'},'ResourceName':'Lock','BikeName':'Lock','Date':'2018.11.19_10:29:22'}}},{'BikeName':'Bike3','Resources':{'Speed':{'ResourceData':{'format':'m/s','value':3.4},'ResourceName':'Speed','BikeName':'Bike1','Date':'2018.11.18_18:26:19'},'Temperature':{'ResourceData':{'format':'C','value':22.3},'ResourceName':'Temperature','BikeName':'Bike1','Date':'2018.11.18_18:27:15'},'TyrePressure':{'ResourceData':{'format':'bar','value':1.3},'ResourceName':'TyrePressure','BikeName':'Bike1','Date':'2018.11.18_18:26:17'},'User':{},'AirQuality':{},'Humidity':{'ResourceData':{'format':'%','value':80.5},'ResourceName':'Humidity','BikeName':'Bike1','Date':'2018.11.18_18:27:16'},'Odometer':{'ResourceData':{'format':'Km','value':12.6},'ResourceName':'Odometer','BikeName':'Bike1','Date':'2018.11.18_18:26:18'},'NFC':{},'Sampling_Period':{},'GPS':{'ResourceData':{'format':'DD','value':{'lat':43.7167,'long':10.3956}},'ResourceName':'GPS','BikeName':'Bike1','Date':'2018.11.18_18:27:13'},'Lock':{'ResourceData':{'format':'boolean','value':'true'},'ResourceName':'Lock','BikeName':'Lock','Date':'2018.11.19_10:29:22'}}},{'BikeName':'Bike4','Resources':{'Speed':{'ResourceData':{'format':'m/s','value':2.4},'ResourceName':'Speed','BikeName':'Bike1','Date':'2018.11.18_18:26:19'},'Temperature':{'ResourceData':{'format':'C','value':22.3},'ResourceName':'Temperature','BikeName':'Bike1','Date':'2018.11.18_18:27:15'},'TyrePressure':{'ResourceData':{'format':'bar','value':1.3},'ResourceName':'TyrePressure','BikeName':'Bike1','Date':'2018.11.18_18:26:17'},'User':{},'AirQuality':{},'Humidity':{'ResourceData':{'format':'%','value':80.5},'ResourceName':'Humidity','BikeName':'Bike1','Date':'2018.11.18_18:27:16'},'Odometer':{'ResourceData':{'format':'Km','value':12.6},'ResourceName':'Odometer','BikeName':'Bike1','Date':'2018.11.18_18:26:18'},'NFC':{},'Sampling_Period':{},'GPS':{'ResourceData':{'format':'DD','value':{'lat':43.7141,'long':10.3977}},'ResourceName':'GPS','BikeName':'Bike1','Date':'2018.11.18_18:27:13'},'Lock':{'ResourceData':{'format':'boolean','value':'true'},'ResourceName':'Lock','BikeName':'Lock','Date':'2018.11.19_10:29:22'}}},{'BikeName':'Bike1','Resources':{'Speed':{'ResourceData':{'format':'m/s','value':5.4},'ResourceName':'Speed','BikeName':'Bike1','Date':'2018.11.18_18:26:19'},'Temperature':{'ResourceData':{'format':'C','value':22.3},'ResourceName':'Temperature','BikeName':'Bike1','Date':'2018.11.18_18:27:15'},'TyrePressure':{'ResourceData':{'format':'bar','value':1.3},'ResourceName':'TyrePressure','BikeName':'Bike1','Date':'2018.11.18_18:26:17'},'User':{},'AirQuality':{},'Humidity':{'ResourceData':{'format':'%','value':80.5},'ResourceName':'Humidity','BikeName':'Bike1','Date':'2018.11.18_18:27:16'},'Odometer':{'ResourceData':{'format':'Km','value':12.6},'ResourceName':'Odometer','BikeName':'Bike1','Date':'2018.11.18_18:26:18'},'NFC':{},'Sampling_Period':{},'GPS':{'ResourceData':{'format':'DD','value':{'lat':43.7158,'long':10.3987}},'ResourceName':'GPS','BikeName':'Bike1','Date':'2018.11.18_18:27:13'},'Lock':{'ResourceData':{'format':'boolean','value':'true'},'ResourceName':'Lock','BikeName':'Lock','Date':'2018.11.19_10:29:22'}}}];";
				json_array = new JSONArray(bikeArray);
			}
		}
		
		response.setContentType("json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json_array.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
