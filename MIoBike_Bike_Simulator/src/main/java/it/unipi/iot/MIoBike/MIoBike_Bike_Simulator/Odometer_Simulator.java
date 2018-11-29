package it.unipi.iot.MIoBike.MIoBike_Bike_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class Odometer_Simulator extends CoapResource {
	private String distance = "0";

	public Odometer_Simulator() {
		super("Odometer");	
		getAttributes().setTitle("Odometer");
		setObservable(true);
	}
	 
	public void handleGET(CoapExchange exchange) {
		JSONObject response = new JSONObject();
		response.put("format", "Km");
		response.put("value", distance);
		exchange.respond(response.toString());
		System.out.println("New Distance: "+distance);
	}
	
	public void handlePOST(CoapExchange exchange) {		// external simulation
		exchange.respond(ResponseCode.CREATED);
		byte[] content = exchange.getRequestPayload();
		String contentStr = new String(content);
		JSONObject request = new JSONObject(contentStr);
		distance = request.getString("distance");
	}

}