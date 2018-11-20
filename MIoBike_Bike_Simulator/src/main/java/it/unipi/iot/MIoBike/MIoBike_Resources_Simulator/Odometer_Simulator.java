package it.unipi.iot.MIoBike.MIoBike_Resources_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class Odometer_Simulator extends CoapResource {
	private double kilometers = 12.6;
	public Odometer_Simulator() {
		super("Odometer");	
		getAttributes().setTitle("Odometer");
		setObservable(true);
	}
	 
	public void handleGET(CoapExchange exchange) {
		JSONObject response = new JSONObject();
		response.put("format", "Km");
		response.put("value", kilometers);
		exchange.respond(response.toString());

	}

}