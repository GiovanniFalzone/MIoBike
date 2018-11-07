package it.unipi.iot.MIoBike.MIoBike_MN_ADN;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;
import it.unipi.iot.MIoBike.MIoBike_Utils.Manager_om2m;

public class Resource_Manager extends Thread{
	private CoapClient my_coap_client;
	private int period;
	private JSONObject last_read;
	private Manager_om2m my_om2m_MN_manager;
	private String Container_Name;
	private String Res_Name;
	private String NotificationManager_name;
	
	public Resource_Manager(String resource_uri, Manager_om2m my_om2m_MN_manager, int period, String Res_Name, String Container_Name, String NotificationManager) {
		this.my_coap_client = new CoapClient(resource_uri);	
		this.period = period;
		this.my_om2m_MN_manager = my_om2m_MN_manager;
		this.Res_Name = Res_Name;
		this.Container_Name = Container_Name;
		this.NotificationManager_name = NotificationManager;
		this.my_om2m_MN_manager.create_Container(this.Res_Name, this.Container_Name);
		String res_path = "/"+Res_Name+"-mn-cse/"+Res_Name+"-mn-name/"+Res_Name+"/"+Container_Name;
		this.my_om2m_MN_manager.create_Subscription("coap://127.0.0.1:5683/~" + res_path, "coap://127.0.0.1:5685/"+NotificationManager_name, NotificationManager_name);
	}

	private JSONObject read_sensor() {
		String res_str;
		JSONObject res;
		CoapResponse response = my_coap_client.get();
		if (response!=null) {
			res_str = response.getResponseText();
			res = new JSONObject(res_str);
			String date_str = new SimpleDateFormat("HH:mm:ss").format(new Date());
			String Instance_Name = Res_Name + "_" + Container_Name + "_" + date_str;
			String read_value = res.get("value").toString();
			this.my_om2m_MN_manager.create_Content_Instance(this.Res_Name, this.Container_Name, Instance_Name, read_value);
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

	public void handle_Notification() {}
	
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
