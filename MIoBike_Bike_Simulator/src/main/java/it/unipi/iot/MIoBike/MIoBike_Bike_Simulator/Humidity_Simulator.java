package it.unipi.iot.MIoBike.MIoBike_Bike_Simulator;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;

class Humidity_Simulator extends CoapResource {
	private String humidity = "80.5";
	
	public Humidity_Simulator() {
		super("Humidity");	
		getAttributes().setTitle("Humidity");
		setObservable(true);
	}
	 
	public void handleGET(CoapExchange exchange) {
		JSONObject response = new JSONObject();
		response.put("format", "%");
		response.put("value", ""+humidity);
		exchange.respond(response.toString());
		System.out.println("New humidity: "+humidity);
	}
	
	public void handlePOST(CoapExchange exchange) {		// external simulation
		exchange.respond(ResponseCode.CREATED);
		byte[] content = exchange.getRequestPayload();
		String contentStr = new String(content);
		JSONObject request = new JSONObject(contentStr);
		humidity = request.getString("humidity");
	}

}