package it.unipi.iot.MIoBike.MIoBike_Bike_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class Speed_Simulator extends CoapResource {
	private String speed = "5.4";
	
	public Speed_Simulator() {
		super("Speed");	
		getAttributes().setTitle("Speed");
		setObservable(true);
	}
	 
	public void handleGET(CoapExchange exchange) {
		JSONObject response = new JSONObject();
		response.put("format", "Km/h");
		response.put("value", speed);
		exchange.respond(response.toString());
	}

	public void handlePOST(CoapExchange exchange) {		// external simulation
		exchange.respond(ResponseCode.CREATED);
		byte[] content = exchange.getRequestPayload();
		String contentStr = new String(content);
		JSONObject request = new JSONObject(contentStr);
		speed = request.getString("speed");
		System.out.println("New Speed: "+speed);
	}

}