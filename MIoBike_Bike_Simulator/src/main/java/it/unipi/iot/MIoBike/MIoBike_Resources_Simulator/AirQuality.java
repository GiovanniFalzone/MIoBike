package it.unipi.iot.MIoBike.MIoBike_Resources_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class AirQuality extends CoapResource {
	private double AirQuality = 80.5;
	public AirQuality() {
		super("Humidity");	
		getAttributes().setTitle("Humidity");
		setObservable(true);
	}
	 
	public void handleGET(CoapExchange exchange) {
		JSONObject response = new JSONObject();
		response.put("format", "%");
		response.put("value", AirQuality);
		exchange.respond(response.toString());
	}

}