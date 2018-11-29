package it.unipi.iot.MIoBike.MIoBike_common;

import static it.unipi.iot.MIoBike.MIoBike_common.Constants.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.json.JSONObject;

import it.unipi.iot.MIoBike.MIoBike_common.om2m_Node_Manager;

public class Sensor_Manager_periodic extends Resource_Manager{
	public Sensor_Manager_periodic(String resource_uri, om2m_Node_Manager my_om2m_MN_manager, int my_period, String my_Bike_Name, String my_Sensor_Name, String type) {
		super(resource_uri, my_om2m_MN_manager, my_period, my_Bike_Name, my_Sensor_Name, type);
	}

	@Override
	public void execute_Job() {
		String res_str = "";
		my_coap_client = new CoapClient(Resource_uri);
		CoapResponse response = my_coap_client.get();
		if (response!=null && response.getCode()!=ResponseCode._UNKNOWN_SUCCESS_CODE) {
			res_str = response.getResponseText();
			if(SENSORTAG && ((ResourceName == TEMPERATURE_NAME) || (ResourceName == HUMIDITY_NAME))) {
				if(DEV_MODE) {
					System.out.println("SensorTag response: "+res_str);					
				}
				JSONObject content = new JSONObject();
				String value = "";
				String format = "";
				if(ResourceName == TEMPERATURE_NAME) {
					String split = res_str.split("Temp=")[1];
					value = split.split(" ")[0];
					format = split.split(" ")[1].trim();
				} else if(ResourceName == HUMIDITY_NAME) {
					String split = res_str.split("Humidity=")[1];
					value = split.split(" ")[0];
					format = split.split(" ")[1].trim();
				}
				content.put("format", format);
				content.put("value", value);
				res_str = content.toString();
			}
			String date_str = "";
			SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
			sdfLocal.setTimeZone(TimeZone.getTimeZone("GMT"));
		    date_str = sdfLocal.format(new Date());
			
			json_data.put(JSON_KEY_DATE, date_str);
			json_data.put(JSON_KEY_RESOURCEDATA, new JSONObject(res_str));
			
			String cnf = "JSON";
			String con = json_data.toString();
			if(DEV_MODE) {
				System.out.println(con);
			}

			String [] labels = om2m_MN_manager.get_labels_from(BikeName, "", "");
			String UserId = "";
			for(String s : labels) {
				if(s.contains(LABEL_PREFIX_USERID)) {
					if(s.split(":").length > 1) {
						UserId = s.split(":")[1];
					}
				}
			}
			om2m_MN_manager.create_Content_Instance(UserId, BikeName, ResourceName, type, cnf, con, labels);
		} else {
			if(DEV_MODE) {
				System.out.println("Error:: no response received");
			}

		}

	}

}
