package it.unipi.iot.MIoBike.MIoBike_MN_ADN;

import static it.unipi.iot.MIoBike.MIoBike_MN_ADN.MN_Constants.Bike_Name;
import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.IN_NM_NAME;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;
import it.unipi.iot.MIoBike.MIoBike_Utils.om2m_Node_Manager;

public class Resource_Manager extends Thread{
	private CoapClient my_coap_client;
	private int period;
	private JSONObject last_read;
	private om2m_Node_Manager my_om2m_MN_manager;
	private String Container_Name;
	private String Res_Name;

	
	public Resource_Manager(String resource_uri, om2m_Node_Manager my_om2m_MN_manager, int period, String Res_Name, String Container_Name) {
		this.my_coap_client = new CoapClient(resource_uri);	
		this.period = period;
		this.my_om2m_MN_manager = my_om2m_MN_manager;
		this.Res_Name = Res_Name;
		this.Container_Name = Container_Name;
		this.my_om2m_MN_manager.create_Container(this.Res_Name, this.Container_Name);
	}

	private JSONObject read_sensor() {
		String res_str;
		JSONObject res;
		JSONObject root;
		CoapResponse response = my_coap_client.get();
		if (response!=null) {
			res_str = response.getResponseText();
			res = new JSONObject(res_str);
			String date_str = new SimpleDateFormat("HH:mm:ss").format(new Date());
			String Instance_Name = Res_Name + "/" + Container_Name + "_" + date_str;
			String read_value = res.get("value").toString();
			root = new JSONObject();
			root.put(Res_Name + "/" + Container_Name, read_value);
			String Content_value = root.toString();
			this.my_om2m_MN_manager.create_Content_Instance(this.Res_Name, this.Container_Name, Instance_Name, Content_value);

			System.out.println(response.getCode());
			System.out.println(response.getOptions());
			System.out.println(res_str);

		} else {
			System.out.println("No response received.");
			res = new JSONObject();
			res.put("val", "null");
		}
		return res;
	}
	
	public void set_period(int new_period) {
		this.period = new_period;
	}

	public JSONObject get_last_read() {
		return last_read;
	}
	
	public void run(){
		int iteration = 0;
		while(true) {
			System.out.println("Iteration " + iteration++);
			try {
				Thread.sleep(this.period);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.last_read = read_sensor();
		}
	}
}
