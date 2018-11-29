package it.unipi.iot.MIoBike.web.servlets;

import static it.unipi.iot.MIoBike.web.Constants_web.CONNECTED;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import it.unipi.iot.MIoBike.MIoBike_BikeManager.BikeManager;

/**
 * Servlet implementation class PeriodicRequestBikesServlet
 * request all bikes data to oM2M to update admin dashboard with data for each bike
 */
@WebServlet("/PeriodicRequestBikesServlet")
public class PeriodicRequestBikesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PeriodicRequestBikesServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/event-stream");
		response.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = response.getWriter();
		BikeManager bm = new BikeManager();
		//Runtime runtime = Runtime.getRuntime();
		while (true) { // this is only for demonstration purpose, don't do this! Use asynchronous APIs instead
			try {
				String bikeArray = null;
				JSONArray json_array = null;
				if(CONNECTED)
					json_array = bm.get_Bikes_Data();
				else {
					//default bike array
					bikeArray = "[{'BikeName':'Bike2','Resources':{'Speed':{},'Temperature':{},'TyrePressure':{},'User':{},'AirQuality':{},'Humidity':{},'NFC':{},'Odometer':{},'Sampling_Period':{},'GPS':{'ResourceData':{'format':'DD','value':{'lat':43.7158,'long':10.4000}},'ResourceName':'GPS','BikeName':'Bike1','Date':'2018.11.18_18:27:13'},'Lock':{'ResourceData':{'format':'boolean','value':'true'},'ResourceName':'Lock','BikeName':'Lock','Date':'2018.11.19_10:29:22'}}},{'BikeName':'Bike3','Resources':{'Speed':{'ResourceData':{'format':'m/s','value':3.4},'ResourceName':'Speed','BikeName':'Bike1','Date':'2018.11.18_18:26:19'},'Temperature':{'ResourceData':{'format':'C','value':22.3},'ResourceName':'Temperature','BikeName':'Bike1','Date':'2018.11.18_18:27:15'},'TyrePressure':{'ResourceData':{'format':'bar','value':1.3},'ResourceName':'TyrePressure','BikeName':'Bike1','Date':'2018.11.18_18:26:17'},'User':{},'AirQuality':{},'Humidity':{'ResourceData':{'format':'%','value':80.5},'ResourceName':'Humidity','BikeName':'Bike1','Date':'2018.11.18_18:27:16'},'Odometer':{'ResourceData':{'format':'Km','value':12.6},'ResourceName':'Odometer','BikeName':'Bike1','Date':'2018.11.18_18:26:18'},'NFC':{},'Sampling_Period':{},'GPS':{'ResourceData':{'format':'DD','value':{'lat':43.7167,'long':10.3956}},'ResourceName':'GPS','BikeName':'Bike1','Date':'2018.11.18_18:27:13'},'Lock':{'ResourceData':{'format':'boolean','value':'true'},'ResourceName':'Lock','BikeName':'Lock','Date':'2018.11.19_10:29:22'}}},{'BikeName':'Bike4','Resources':{'Speed':{'ResourceData':{'format':'m/s','value':2.4},'ResourceName':'Speed','BikeName':'Bike1','Date':'2018.11.18_18:26:19'},'Temperature':{'ResourceData':{'format':'C','value':22.3},'ResourceName':'Temperature','BikeName':'Bike1','Date':'2018.11.18_18:27:15'},'TyrePressure':{'ResourceData':{'format':'bar','value':1.3},'ResourceName':'TyrePressure','BikeName':'Bike1','Date':'2018.11.18_18:26:17'},'User':{},'AirQuality':{},'Humidity':{'ResourceData':{'format':'%','value':80.5},'ResourceName':'Humidity','BikeName':'Bike1','Date':'2018.11.18_18:27:16'},'Odometer':{'ResourceData':{'format':'Km','value':12.6},'ResourceName':'Odometer','BikeName':'Bike1','Date':'2018.11.18_18:26:18'},'NFC':{},'Sampling_Period':{},'GPS':{'ResourceData':{'format':'DD','value':{'lat':43.7141,'long':10.3977}},'ResourceName':'GPS','BikeName':'Bike1','Date':'2018.11.18_18:27:13'},'Lock':{'ResourceData':{'format':'boolean','value':'false'},'ResourceName':'Lock','BikeName':'Lock','Date':'2018.11.19_10:29:22'}}},{'BikeName':'Bike1','Resources':{'Speed':{'ResourceData':{'format':'m/s','value':5.4},'ResourceName':'Speed','BikeName':'Bike1','Date':'2018.11.18_18:26:19'},'Temperature':{'ResourceData':{'format':'C','value':22.3},'ResourceName':'Temperature','BikeName':'Bike1','Date':'2018.11.18_18:27:15'},'TyrePressure':{'ResourceData':{'format':'bar','value':1.3},'ResourceName':'TyrePressure','BikeName':'Bike1','Date':'2018.11.18_18:26:17'},'User':{},'AirQuality':{},'Humidity':{'ResourceData':{'format':'%','value':80.5},'ResourceName':'Humidity','BikeName':'Bike1','Date':'2018.11.18_18:27:16'},'Odometer':{'ResourceData':{'format':'Km','value':12.6},'ResourceName':'Odometer','BikeName':'Bike1','Date':'2018.11.18_18:26:18'},'NFC':{},'Sampling_Period':{},'GPS':{'ResourceData':{'format':'DD','value':{'lat':43.7158,'long':10.3987}},'ResourceName':'GPS','BikeName':'Bike1','Date':'2018.11.18_18:27:13'},'Lock':{'ResourceData':{'format':'boolean','value':'false'},'ResourceName':'Lock','BikeName':'Lock','Date':'2018.11.19_10:29:22'}}}];";
					json_array = new JSONArray(bikeArray);
				}
						
				printWriter.write("data: " + json_array.toString() + "\n\n");
				printWriter.flush();
				response.flushBuffer();
				//Do not close the writer!
				//Gives the locked bike every 10 seconds
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
