package it.unipi.iot.MIoBike.MIoBike_Utils;

import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.*;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;

public class Actuator_Manager_periodic extends Resource_Manager{
	private String Status;
	
	public Actuator_Manager_periodic(String resource_uri, om2m_Node_Manager my_om2m_MN_manager,
				int my_period, String my_Bike_Name, String my_Actuator_Name, String my_type) {
		super(resource_uri, my_om2m_MN_manager, my_period, my_Bike_Name, my_Actuator_Name, my_type);
		Status = "true";
	}

	private void send_status(String status) {
		my_coap_client = new CoapClient(Resource_uri+"/?value="+status);
		CoapResponse response = my_coap_client.get();
		if (response!=null) {
			JSONObject res_json = new JSONObject(response.getResponseText());
			String act_status = res_json.getString(JSON_KEY_VALUE);
			if(!act_status.equals(status)) {
				if(DEV_MODE) {
					System.out.println("Actuator Problem");
				}
			}
		}
	}
	
	public void set_status(JSONObject json_con) {
		JSONObject data = json_con.getJSONObject(JSON_KEY_RESOURCEDATA);
		Status = data.getString(JSON_KEY_VALUE);
		send_status(Status);
	}
	
	protected void execute_Job() {
		send_status(Status);
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
