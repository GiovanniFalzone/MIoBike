package it.unipi.iot.MIoBike.MIoBike_common;

import static it.unipi.iot.MIoBike.MIoBike_common.Constants.*;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONObject;

public class Actuator_Manager_periodic extends Resource_Manager{
	private String Status;
	
	public Actuator_Manager_periodic(String resource_uri, om2m_Node_Manager my_om2m_MN_manager,
				int my_period, String my_Bike_Name, String my_Actuator_Name, String my_type) {
		super(resource_uri, my_om2m_MN_manager, my_period, my_Bike_Name, my_Actuator_Name, my_type);
		Status = "true";
	}

	@Override
	public void execute_Job() {
		my_coap_client = new CoapClient(Resource_uri);
		JSONObject payload = new JSONObject();
		payload.put(JSON_KEY_FORMAT, "boolean");
		payload.put(JSON_KEY_VALUE, Status);
		CoapResponse response = my_coap_client.put(payload.toString(), MediaTypeRegistry.APPLICATION_JSON);
		if (response!=null) {
			if(response.getCode() == CoAP.ResponseCode.INTERNAL_SERVER_ERROR) {
				if(DEV_MODE) {
					System.out.println("Actuator Problem");
				}
			}
		}
	}

	@Override
	public void set_status(JSONObject json_con) {
		if(json_con.has(JSON_KEY_RESOURCEDATA)) {
			JSONObject data = json_con.getJSONObject(JSON_KEY_RESOURCEDATA);
			Status = data.getString(JSON_KEY_VALUE);
			execute_Job();
		}
	}
	
}
