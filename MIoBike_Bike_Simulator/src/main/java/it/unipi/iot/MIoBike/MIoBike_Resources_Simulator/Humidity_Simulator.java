package it.unipi.iot.MIoBike.MIoBike_Resources_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class Humidity_Simulator extends CoapResource {
	private double humidity = 80.5;
	public Humidity_Simulator() {
		super("Humidity");	
		getAttributes().setTitle("Humidity");
		setObservable(true);
	}
	 
	public void handleGET(CoapExchange exchange) {
		JSONObject response = new JSONObject();
		response.put("format", "%");
		response.put("value", humidity);
		exchange.respond(response.toString());
	}

}