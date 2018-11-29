package it.unipi.iot.MIoBike.MIoBike_Bike_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class Temperature_Simulator extends CoapResource {
	private String temperature = "22.3";

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
		System.out.println("New Temperature: "+temperature);
	}

	public void handlePOST(CoapExchange exchange) {		// external simulation
		exchange.respond(ResponseCode.CREATED);
		byte[] content = exchange.getRequestPayload();
		String contentStr = new String(content);
		JSONObject request = new JSONObject(contentStr);
		temperature = request.getString("temperature");
	}

}