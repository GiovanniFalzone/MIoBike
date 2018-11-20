package it.unipi.iot.MIoBike.MIoBike_Resources_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class TyrePressure_Simulator extends CoapResource {
	private double pressure = 1.3;
	public TyrePressure_Simulator() {
		super("TyrePressure");	
		getAttributes().setTitle("TyrePressure");
		setObservable(true);
	}
	 
	public void handleGET(CoapExchange exchange) {
		JSONObject response = new JSONObject();
		response.put("format", "bar");
		response.put("value", pressure);
		exchange.respond(response.toString());

	}

}