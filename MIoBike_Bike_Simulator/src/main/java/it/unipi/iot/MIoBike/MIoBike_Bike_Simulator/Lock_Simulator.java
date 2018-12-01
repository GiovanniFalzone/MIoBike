package it.unipi.iot.MIoBike.MIoBike_Bike_Simulator;

import static it.unipi.iot.MIoBike.MIoBike_common.Constants.*;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONException;
import org.json.JSONObject;


class Lock_Simulator extends CoapResource {
	private String lock_status; 
	public Lock_Simulator() {
		super("Lock");	
		lock_status = "true";
		getAttributes().setTitle("Lock");
		setObservable(true);
	}

	public void handlePUT(CoapExchange exchange) {
		String payload = exchange.getRequestText();
		JSONObject response = null;
		try {
	    	response = new JSONObject(payload);
	    } catch (JSONException exception_0) {
	    	System.out.println("not a json");
	    	exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
	    	return;
	    }
		lock_status = response.getString(JSON_KEY_VALUE);
		exchange.respond(CoAP.ResponseCode.CHANGED);
		System.out.println("New Lock Status: "+lock_status);

		String uri = "coap://127.0.0.1:"+BIKE_JAVA_SIMULATOR_PORT+"/PhySim";
		CoapClient my_coap_client = new CoapClient(uri);
		my_coap_client.put(payload, MediaTypeRegistry.APPLICATION_JSON);
	}

}