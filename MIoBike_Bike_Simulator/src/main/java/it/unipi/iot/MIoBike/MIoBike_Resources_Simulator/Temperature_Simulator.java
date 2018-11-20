package it.unipi.iot.MIoBike.MIoBike_Resources_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class Temperature_Simulator extends CoapResource {
	private double temperature = 22.3;
	public Temperature_Simulator() {
		super("Temperature");	
		getAttributes().setTitle("Temperature");
		setObservable(true);
	}
	 
	public void handleGET(CoapExchange exchange) {
		JSONObject response = new JSONObject();
		response.put("format", "C");
		response.put("value", temperature);
		exchange.respond(response.toString());

	}

}