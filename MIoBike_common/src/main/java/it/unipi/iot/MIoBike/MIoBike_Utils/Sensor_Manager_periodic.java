package it.unipi.iot.MIoBike.MIoBike_Utils;

import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;
import it.unipi.iot.MIoBike.MIoBike_Utils.om2m_Node_Manager;

public class Sensor_Manager_periodic extends Resource_Manager{
	public Sensor_Manager_periodic(String resource_uri, om2m_Node_Manager my_om2m_MN_manager, int my_period, String my_Bike_Name, String my_Sensor_Name, String type) {
		super(resource_uri, my_om2m_MN_manager, my_period, my_Bike_Name, my_Sensor_Name, "sensor");
	}

	private void execute_Job() {
		String res_str = "";
		CoapResponse response = my_coap_client.get();
		if (response!=null) {
			res_str = response.getResponseText();
			String date_str = "";
			SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
			sdfLocal.setTimeZone(TimeZone.getTimeZone("GMT"));
		    date_str = sdfLocal.format(new Date());
			
			json_data.put(JSON_KEY_DATE, date_str);
			json_data.put(JSON_KEY_RESOURCEDATA, new JSONObject(res_str));
			
			String cnf = "JSON";
			String con = json_data.toString();
			String [] labels = new String[] { type };
			om2m_MN_manager.create_Content_Instance(BikeName, ResourceName, cnf, con, labels);

			if(DEV_MODE) {
				System.out.println(json_data.toString());
				System.out.println(response.getCode());
				System.out.println(response.getOptions());
				System.out.println(res_str);
			}
		}
	}

	public void run(){
		int iteration = 0;
		while(true) {
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(DEV_MODE) {
				System.out.println("--------------"+BikeName+"/"+ResourceName+" Iteration " + iteration++ + "-------------------------------");
			}
			execute_Job();
			if(DEV_MODE) {
				System.out.println("---------------------------------------------------------------------------");
			}
		}
	}

}
