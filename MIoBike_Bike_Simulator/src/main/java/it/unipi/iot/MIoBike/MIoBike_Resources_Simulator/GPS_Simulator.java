package it.unipi.iot.MIoBike.MIoBike_Resources_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class GPS_Simulator extends CoapResource {
	private double latitude = 43.7158;
	private double longitude = 10.3987;
	public GPS_Simulator() {
		super("GPS");	
		getAttributes().setTitle("GPS");
		setObservable(true);
	}
	 
	public void handleGET(CoapExchange exchange) {
		//"{\"format\":\"DD\", \"value\":{\"lat\":43.7158, \"long\":10.3987} }"
		JSONObject response = new JSONObject();
		response.put("format", "DD");
		JSONObject cords = new JSONObject();
		cords.put("lat", latitude);
		cords.put("long", longitude);
		response.put("value", cords);
		exchange.respond(response.toString());
	}

}