package it.unipi.iot.MIoBike.MIoBike_Resources_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class Speed_Simulator extends CoapResource {
	private double speed = 5.4;
	public Speed_Simulator() {
		super("Speed");	
		getAttributes().setTitle("Speed");
		setObservable(true);
	}
	 
	public void handleGET(CoapExchange exchange) {
		JSONObject response = new JSONObject();
		response.put("format", "m/s");
		response.put("value", speed);
		exchange.respond(response.toString());
	}

}